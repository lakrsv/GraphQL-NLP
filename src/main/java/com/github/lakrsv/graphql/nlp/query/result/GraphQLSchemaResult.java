package com.github.lakrsv.graphql.nlp.query.result;

import static com.github.lakrsv.graphql.nlp.lang.processing.Constants.CLOSED_BRACKET;
import static com.github.lakrsv.graphql.nlp.lang.processing.Constants.CLOSED_PARANTHESES;
import static com.github.lakrsv.graphql.nlp.lang.processing.Constants.COLON;
import static com.github.lakrsv.graphql.nlp.lang.processing.Constants.COMMA;
import static com.github.lakrsv.graphql.nlp.lang.processing.Constants.OPEN_BRACKET;
import static com.github.lakrsv.graphql.nlp.lang.processing.Constants.OPEN_PARANTHESES;
import static com.github.lakrsv.graphql.nlp.lang.processing.Constants.QUOTE;
import static com.github.lakrsv.graphql.nlp.lang.processing.Constants.SPACE;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import com.github.lakrsv.graphql.nlp.exceptions.ArgumentException;
import com.github.lakrsv.graphql.nlp.schema.matchers.MatcherResult;
import com.github.lakrsv.graphql.nlp.schema.traversal.FieldInformation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * {@inheritDoc}
 */
@NonNull
@RequiredArgsConstructor
@Getter
public class GraphQLSchemaResult implements SchemaResult {

  private static final List<String> EMPTY_INNER_PATH = new ArrayList<>();
  private final MatcherResult<FieldInformation> result;
  private List<SchemaResult> children = new ArrayList<>();
  private Float averageScore;

  /**
   * {@inheritDoc}
   */
  public float getAverageScore() {
    if (averageScore != null) {
      return averageScore;
    }

    var allScores = new ArrayList<Integer>();
    var toVisit = new ArrayDeque<SchemaResult>();
    toVisit.add(this);

    if (averageScore == null) {
      while (toVisit.size() > 0) {
        var next = toVisit.pop();
        allScores.add(next.getResult().getSimilarity());
        toVisit.addAll(next.getChildren());
      }
    }
    averageScore =
        (float) allScores.stream().mapToInt(Integer::intValue).sum() / allScores.size();
    return averageScore;
  }

  /**
   * {@inheritDoc}
   *
   * @throws ArgumentException if an argument has a missing required value
   */
  public String toQueryString() {
    var builder = new StringBuilder();
    var toVisit = new LinkedList<SchemaResult>();
    toVisit.add(this);

    builder.append(OPEN_BRACKET);
    var currentDepth = 0;
    while (toVisit.size() > 0) {
      var next = toVisit.pollFirst();
      if (next == null) {
        currentDepth--;
        builder.append(CLOSED_BRACKET);
        continue;
      }
      var result = next.getResult();
      addResultWithArguments(builder, result);

      if (next.getChildren().size() > 0) {

        builder.append(OPEN_BRACKET);

        var childrenByInnerPath = next.getChildren().stream()
            .collect(groupingBy(c -> c.getResult().getInnerPath()));
        var transformedChildren =
            groupChildrenByInnerPathAsSchemaResult(childrenByInnerPath);
        currentDepth++;
        toVisit.push(null);
        transformedChildren.forEach(toVisit::push);
      } else {
        builder.append(SPACE);
      }
    }
    for (var i = 0; i < currentDepth; i++) {
      builder.append(CLOSED_BRACKET);
    }
    builder.append(CLOSED_BRACKET);
    return builder.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SchemaResult removeIncompleteTypes() {
    children =
        children.stream().map(SchemaResult::removeIncompleteTypes).filter(Objects::nonNull)
            .collect(toList());
    if (result.getResult().getChildren().size() > 0 && children.size() == 0) {
      return null;
    }
    return this;
  }

  private void addResultWithArguments(StringBuilder builder,
      MatcherResult<FieldInformation> result) {
    builder.append(result.getResult().getName());
    if (result.getResult().getInputArguments().size() > 0) {
      var inputArguments = result.getResult().getInputArguments();
      var doingArguments = false;
      for (var argument : inputArguments) {
        if (argument.getValue() != null) {
          if (!doingArguments) {
            builder.append(OPEN_PARANTHESES);
            doingArguments = true;
          } else {
            builder.append(COMMA);
            builder.append(SPACE);
          }

          builder.append(argument.getKey());
          builder.append(COLON);
          builder.append(SPACE);

          // TODO - Handle more than only integer and strings
          if (argument.getValue() instanceof String) {
            var strArgumentValue = (String) argument.getValue();
            if (isNumeric(strArgumentValue)) {
              builder.append(argument.getValue());
            } else {
              builder.append(QUOTE);
              builder.append(argument.getValue());
              builder.append(QUOTE);
            }
          } else {
            builder.append(argument.getValue());
          }
        } else if (argument.isRequired()) {
          throw new ArgumentException("Field: " + result.getResult().getName()
              + " has missing value for the required argument: " + argument.getKey());
        }
      }
      if (doingArguments) {
        builder.append(CLOSED_PARANTHESES);
      }
    }
  }

  private List<SchemaResult> groupChildrenByInnerPathAsSchemaResult(
      Map<List<FieldInformation>, List<SchemaResult>> schemaResultsByInnerPath) {
    return schemaResultsByInnerPath.entrySet().stream().map(e -> {
      if (e.getKey().size() == 0) {
        return e.getValue();
      }
      InnerSchemaResult rootResult = null;
      InnerSchemaResult currentResult = null;
      for (var field : e.getKey()) {
        var newResult = new InnerSchemaResult(field);
        if (rootResult == null) {
          rootResult = newResult;
        } else {
          currentResult.addChild(newResult);
        }
        currentResult = newResult;
      }
      e.getValue().stream().peek(v -> v.getResult().getInnerPath().clear())
          .forEach(currentResult::addChild);
      return List.of(rootResult);
    }).flatMap(Collection::stream).collect(toList());
  }

  @RequiredArgsConstructor
  private class InnerSchemaResult implements SchemaResult {

    private final FieldInformation fieldInformation;
    private List<SchemaResult> children = new ArrayList<>();

    @Override
    public MatcherResult<FieldInformation> getResult() {
      return new MatcherResult<>("", 100, fieldInformation);
    }

    @Override
    public List<SchemaResult> getChildren() {
      return children;
    }

    @Override
    public float getAverageScore() {
      return 0;
    }

    @Override
    public String toQueryString() {
      return "";
    }

    @Override
    public SchemaResult removeIncompleteTypes() {
      children =
          children.stream().map(SchemaResult::removeIncompleteTypes).filter(Objects::nonNull)
              .collect(toList());
      if (fieldInformation.getChildren().size() > 0 && children.size() == 0) {
        return null;
      }
      return this;
    }

    void addChild(SchemaResult child) {
      children.add(child);
    }
  }
}
