package com.github.lakrsv.graphql.nlp.schema.matchers;

import static java.util.Collections.emptyList;

import com.github.lakrsv.graphql.nlp.schema.traversal.FieldInformation;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Responsible for executing field matchers until a match has been found
 */
@Builder
@Slf4j
public class ChainedFieldMatcher implements FieldMatcher {

  /**
   * The default matcher to use
   */
  @NonNull
  @Builder.Default
  private final FieldMatcher defaultMatcher = new KeyScoreFieldMatcher();

  /**
   * The matchers to use. These should be supplied in the order that they should be executed in
   */
  @NonNull
  @Builder.Default
  private final FieldMatcher[] customMatchers = new FieldMatcher[0];

  /**
   * {@inheritDoc}
   */
  public List<MatcherResult<FieldInformation>> getClosestMatchingChildren(String target,
      Map<String, FieldInformation> typeMap, MatchOptionFactory matchOptionFactory) {

    List<MatcherResult<FieldInformation>> results = emptyList();
    if (customMatchers.length == 0) {
      results = defaultMatcher
          .getClosestMatchingChildren(target, typeMap, matchOptionFactory);
      return results;
    }

    for (var customMatcher : customMatchers) {
      results = customMatcher.getClosestMatchingChildren(target, typeMap, matchOptionFactory);
      if (!results.isEmpty()) {
        break;
      }
    }
    return results;
  }
}
