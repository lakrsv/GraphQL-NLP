package com.github.lakrsv.graphql.nlp.schema.matchers;

import com.github.lakrsv.graphql.nlp.schema.traversal.FieldInformation;
import java.util.List;
import java.util.Map;

/**
 * Responsible for performing field matching on a {@link FieldInformation}
 */
public interface FieldMatcher {

  /**
   * Gets the closest matches from the current level of the schema map provided
   *
   * @param target The target string to match
   * @param typeMap The type map to match against
   * @param matchOptionFactory The {@link MatchOptionFactory} with specific options for the field matcher
   * @return A list of {@link MatcherResult} with {@link FieldInformation} matches
   */
  List<MatcherResult<FieldInformation>> getClosestMatchingChildren(String target,
      Map<String, FieldInformation> typeMap, MatchOptionFactory matchOptionFactory);
}
