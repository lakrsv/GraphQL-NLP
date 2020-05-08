package com.github.lakrsv.graphql.nlp.schema;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.github.lakrsv.graphql.nlp.lang.processing.ProcessedChunk;
import com.github.lakrsv.graphql.nlp.query.QueryGeneratorImpl;
import com.github.lakrsv.graphql.nlp.query.result.GraphQLSchemaResult;
import com.github.lakrsv.graphql.nlp.query.result.SchemaResult;
import com.github.lakrsv.graphql.nlp.schema.argument.DefaultTypeArguments;
import com.github.lakrsv.graphql.nlp.schema.context.ContextMapper;
import com.github.lakrsv.graphql.nlp.schema.context.ContextMapperImpl;
import com.github.lakrsv.graphql.nlp.schema.context.ContextProcessor;
import com.github.lakrsv.graphql.nlp.schema.context.TypeContext;
import com.github.lakrsv.graphql.nlp.schema.context.actions.ContextProcessorAction;
import com.github.lakrsv.graphql.nlp.schema.matchers.ChainedFieldMatcher;
import com.github.lakrsv.graphql.nlp.schema.matchers.MatchOptionFactory;
import com.github.lakrsv.graphql.nlp.schema.argument.InputArgument;
import com.github.lakrsv.graphql.nlp.schema.matchers.MatcherResult;
import com.github.lakrsv.graphql.nlp.schema.traversal.SchemaMapTransformer;
import graphql.ExecutionResult;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.xdrop.fuzzywuzzy.FuzzySearch;

/**
 * Entry-point for schema traversal with a given query
 */
@RequiredArgsConstructor
@Builder
@Slf4j
public class SchemaQueryMapper {

  @NonNull
  private final SchemaMapTransformer schemaMapTransformer;
  @NonNull
  private final ChainedFieldMatcher chainedFieldMatcher;
  @NonNull
  private final Map<String, DefaultTypeArguments> defaultTypeArguments;
  private final ContextMapper contextMapper;

  /**
   * Gets the {@link SchemaResult}s matching the supplied processed user input
   *
   * @param processedChunks The {@link ProcessedChunk}s to match against the schema
   * @param entryPoint The top-level field to start traversal at, or null if it should start at top-level
   * @param matchOptionFactory The factory providing specific match options for field matchers
   * @return A list of matching {@link SchemaResult}s
   */
  public List<SchemaResult> getSchemaResults(List<ProcessedChunk> processedChunks,
      Map<String, DefaultTypeArguments> queryTypeArguments,
      String entryPoint, MatchOptionFactory matchOptionFactory) {
    var nextMap = schemaMapTransformer.lazyGetGraphQLSchemaMap();

    GraphQLSchemaResult rootSchemaResult = new GraphQLSchemaResult(null);
    var schemaResults = new ArrayDeque<GraphQLSchemaResult>();
    schemaResults.add(rootSchemaResult);

    if (entryPoint != null) {
      var entryPointField = nextMap.get(entryPoint);
      var entryPointResult =
          new GraphQLSchemaResult(new MatcherResult<>(entryPoint, 100, entryPointField));
      nextMap = Map.of(entryPointField.getName(), entryPointField);
      rootSchemaResult.getChildren().add(entryPointResult);
      schemaResults.add(entryPointResult);

      var inputArguments = entryPointResult.getResult().getResult().getInputArguments();
      if (inputArguments.size() > 0) {
        populateArguments(entryPointResult, defaultTypeArguments, inputArguments);
        populateArguments(entryPointResult, queryTypeArguments, inputArguments);
      }
    }

    var previousSchemaResults = new ArrayDeque<ArrayDeque<GraphQLSchemaResult>>();

    for (int i = 0; i < processedChunks.size(); i++) {
      var chunk = processedChunks.get(i);
      var newSchemaResults = new ArrayDeque<GraphQLSchemaResult>();
      if (schemaResults == null) {
        break;
      }
      var currentSchemaResults = schemaResults.clone();
      while (currentSchemaResults.size() > 0) {
        var currentSchemaResult = currentSchemaResults.pop();
        if (currentSchemaResult.getResult() != null) {
          nextMap = currentSchemaResult.getResult().getResult().getChildren();
        }

        if (nextMap.size() == 0) {
          continue;
        }

        for (var object : chunk.getObjects()) {

          var matches = chainedFieldMatcher
              .getClosestMatchingChildren(object, nextMap, matchOptionFactory);
          for (var match : matches) {
            var schemaResult = new GraphQLSchemaResult(match);
            currentSchemaResult.getChildren().add(schemaResult);
            newSchemaResults.push(schemaResult);

            // TODO - Cleanup
            var inputArguments =
                schemaResult.getResult().getResult().getInputArguments();
            if (inputArguments.size() > 0) {
              if (chunk.getArguments().size() > 0) {
                addSuppliedArguments(chunk, inputArguments,
                    matchOptionFactory.getDefaultMatchOptions()
                        .getMinimumSimilarity());
              } else {
                populateArguments(schemaResult, defaultTypeArguments, inputArguments);
              }
              populateArguments(schemaResult, queryTypeArguments, inputArguments);
            }

            // Add the specific arguments
          }

          if (contextMapper != null) {
            if (currentSchemaResult.getResult() != null) {
              var contextProcessor = contextMapper.getContextProcessor(
                  currentSchemaResult.getResult().getResult().getName(), object);
              if (contextProcessor != null) {
                for (var action : contextProcessor.getActions()) {
                  currentSchemaResult = action.preProcess(currentSchemaResult);
                }
              }
            }
          }
        }
      }

      if (newSchemaResults.isEmpty()) {
        log.info("New schema results are empty for " + chunk.getObjects());
        schemaResults = previousSchemaResults.pollLast();
        i--;
      } else {
        previousSchemaResults.add(schemaResults);
        schemaResults = newSchemaResults;
      }
    }
    return removeIncompleteTypes(rootSchemaResult.getChildren());
  }

  private void populateArguments(GraphQLSchemaResult schemaResult, Map<String, DefaultTypeArguments> queryTypeArguments,
      List<InputArgument> inputArguments) {
    if (queryTypeArguments.containsKey(schemaResult.getResult().getResult().getName())) {
      var queryArgumentForType = queryTypeArguments.get(schemaResult.getResult().getResult().getName());
      for (var inputArgument : inputArguments) {
        if (queryArgumentForType.getArgumentsByKey()
            .containsKey(inputArgument.getKey())) {
          inputArgument.setValue(
              queryArgumentForType.getArgumentsByKey().get(inputArgument.getKey()));
        }
      }
    }
  }

  private void addSuppliedArguments(ProcessedChunk chunk, List<InputArgument> inputArguments,
      int minimumSimilarity) {
    if (inputArguments.size() == 1 && chunk.getArguments().size() == 1) {
      inputArguments.get(0).setValue(chunk.getArguments().get(0).getValue());
    } else {
      var requiredArguments =
          inputArguments.stream().filter(InputArgument::isRequired).collect(toList());
      var optionalArguments =
          inputArguments.stream().filter(not(InputArgument::isRequired)).collect(toList());
      if (requiredArguments.size() == 1 && chunk.getArguments().size() == 1) {
        requiredArguments.get(0).setValue(chunk.getArguments().get(0).getValue());
      } else {
        var requiredArgumentsByKey =
            requiredArguments.stream().collect(toMap(InputArgument::getKey, arg -> arg));
        var optionalArgumentsByKey =
            optionalArguments.stream().collect(toMap(InputArgument::getKey, arg -> arg));
        var inputArgumentsByKey =
            inputArguments.stream().collect(toMap(InputArgument::getKey, arg -> arg));
        // TODO - Handle matching of argument to value
        for (var chunkArgument : chunk.getArguments()) {
          var match = FuzzySearch
              .extractTop(chunkArgument.getKey(), requiredArgumentsByKey.keySet(), 1);

          if (match.isEmpty()) {
            match = FuzzySearch
                .extractTop(chunkArgument.getKey(), optionalArgumentsByKey.keySet(), 1);
            if (match.isEmpty()) {
              continue;
            }
          }

          var matchResult = match.get(0);
          if (matchResult.getScore() >= minimumSimilarity) {
            inputArgumentsByKey.get(matchResult.getString())
                .setValue(chunkArgument.getValue());
          }
        }
      }
    }
  }

  private List<SchemaResult> removeIncompleteTypes(List<SchemaResult> schemaResults) {
    return schemaResults.stream().map(SchemaResult::removeIncompleteTypes)
        .filter(Objects::nonNull).collect(toList());
  }
}
