package com.github.lakrsv.graphql.nlp.query.result;

import com.github.lakrsv.graphql.nlp.schema.argument.InputArgument;
import com.github.lakrsv.graphql.nlp.schema.matchers.MatcherResult;
import com.github.lakrsv.graphql.nlp.schema.traversal.FieldInformation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A specific result from the schema, which can be used to construct a GraphQL query
 */
public interface SchemaResult {

  /**
   * The inner matcher result
   *
   * @return The {@link MatcherResult} containing the matched {@link FieldInformation} at this level
   */
  MatcherResult<FieldInformation> getResult();

  /**
   * The children results
   *
   * @return The {@link SchemaResult} children of the matched graph
   */
  List<SchemaResult> getChildren();

  /**
   * The average match score of the {@link SchemaResult}, calculated using the top-level field and children of the
   * result
   *
   * @return The average score of the match
   */
  float getAverageScore();

  /**
   * Converts the {@link SchemaResult} into an executable GraphQL query
   * <p>
   * Prior to executing this method, {@link SchemaResult#getMissingRequiredArgumentsByField()} should be called to check
   * if there are missing required argument values
   *
   * @return An executable GraphQL query string
   */
  String toQueryString();

  /**
   * Checks if there are missing values for required {@link InputArgument}s
   *
   * @return A list of {@link InputArgument} that have missing values, grouped by the parent {@link FieldInformation}
   */
  default Map<FieldInformation, List<InputArgument>> getMissingRequiredArgumentsByField() {
    var fieldsWithMissingValues = new HashMap<FieldInformation, List<InputArgument>>();

    var requiredArgumentsWithMissingValues =
        getResult().getResult().getRequiredArgumentsWithMissingValues();
    if (!requiredArgumentsWithMissingValues.isEmpty()) {
      fieldsWithMissingValues
          .put(getResult().getResult(), requiredArgumentsWithMissingValues);
    }

    for (var child : getChildren()) {
      var missingRequiredArgumentsForChild = child.getMissingRequiredArgumentsByField();
      if (!missingRequiredArgumentsForChild.isEmpty()) {
        fieldsWithMissingValues.putAll(missingRequiredArgumentsForChild);
      }
    }
    return fieldsWithMissingValues;
  }

  /**
   * Removes any incomplete matches from the schema, where the field is a non-scalar type and does not have children
   *
   * @return The {@link SchemaResult} with incomplete children removed, or null if the parent is incomplete
   */
  SchemaResult removeIncompleteTypes();
}
