package com.github.lakrsv.graphql.nlp.query;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

import com.github.lakrsv.graphql.nlp.lang.processing.LanguageProcessor;
import com.github.lakrsv.graphql.nlp.query.request.QueryRequest;
import com.github.lakrsv.graphql.nlp.query.result.QueryResult;
import com.github.lakrsv.graphql.nlp.schema.SchemaQueryMapper;
import com.github.lakrsv.graphql.nlp.schema.argument.DefaultTypeArguments;
import com.github.lakrsv.graphql.nlp.schema.argument.GraphQLTypeMapper;
import com.github.lakrsv.graphql.nlp.schema.context.ContextMapper;
import com.github.lakrsv.graphql.nlp.schema.matchers.ChainedFieldMatcher;
import com.github.lakrsv.graphql.nlp.schema.matchers.MatchOptionFactory;
import com.github.lakrsv.graphql.nlp.schema.traversal.SchemaMapTransformer;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeTraverser;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * {@inheritDoc}
 */
@Builder
@Slf4j
public class QueryGeneratorImpl implements QueryGenerator {

  @NonNull
  private final GraphQLSchema schema;
  @NonNull
  @Builder.Default
  private final String queryTypeName = "Query";
  @NonNull
  @Builder.Default
  private final Map<String, Set<String>> synonyms = emptyMap();
  @NonNull
  @Builder.Default
  private final LanguageProcessor languageProcessor = LanguageProcessor.DEFAULT();
  @NonNull
  @Builder.Default
  private final GraphQLTypeMapper graphQLTypeMapper = GraphQLTypeMapper.DEFAULT_TYPE_MAPPER;
  @NonNull
  @Builder.Default
  private final ChainedFieldMatcher chainedFieldMatcher = ChainedFieldMatcher.builder().build();
  @Builder.Default
  private final List<DefaultTypeArguments> defaultTypeArguments = emptyList();
  private final ContextMapper contextMapper;
  private SchemaQueryMapper schemaQueryMapper;

  /**
   * {@inheritDoc}
   */
  public QueryResult convertToQuery(QueryRequest queryRequest) {
    initialize();

    var processedSentence = languageProcessor.process(queryRequest.getText(), synonyms);
    var chunks = processedSentence.getProcessedChunks();
    //var tags = processedSentence.getTags();

    // TODO - Abstract this further inside the schemaQueryMapper
    // TODO - Handle input parameters
    // TODO - Handle strange query navigation? (Bruteforce?)
    // TODO - Implement contextual mapping of queries (for example, "Show me my healthy assets" would interpret healthy in context) (This may be machine-learning territory)
    var results = schemaQueryMapper
        .getSchemaResults(chunks,
            queryRequest.getOptions().getQueryTypeArguments().stream()
                .collect(toMap(DefaultTypeArguments::getTypeName, a -> a)),
            queryRequest.getOptions().getEntryPoint(),
            new MatchOptionFactory(queryRequest.getOptions().getDefaultMatchOptions(),
                queryRequest.getOptions().getSpecificMatchOptions()));

    for (var result : results) {
      log.info("Average score for result " + result.getResult() + " was " + result
          .getAverageScore());
    }

    return new QueryResult(results);
  }

  private void initialize() {
    if (schemaQueryMapper == null) {
      schemaQueryMapper = SchemaQueryMapper.builder().chainedFieldMatcher(chainedFieldMatcher)
          .schemaMapTransformer(
              new SchemaMapTransformer(queryTypeName, new TypeTraverser(), schema))
          .contextMapper(contextMapper).defaultTypeArguments(defaultTypeArguments.stream()
              .collect(toMap(DefaultTypeArguments::getTypeName, a -> a))).build();
    }
  }
}
