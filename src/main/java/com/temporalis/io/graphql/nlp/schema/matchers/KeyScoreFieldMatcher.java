package com.temporalis.io.graphql.nlp.schema.matchers;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import com.temporalis.io.graphql.nlp.schema.traversal.FieldInformation;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import me.xdrop.fuzzywuzzy.FuzzySearch;

/**
 * {@inheritDoc}
 * <p>
 * Basic string matching matcher, the default matcher used
 */
@Slf4j
public class KeyScoreFieldMatcher implements FieldMatcher {

  /**
   * {@inheritDoc}
   */
  @Override
  public List<MatcherResult<FieldInformation>> getClosestMatchingChildren(String target,
      Map<String, FieldInformation> typeMap, MatchOptionFactory matchOptionFactory) {

    if (target == null || target.isEmpty()) {
      return emptyList();
    }

    var matchOptions = matchOptionFactory.getMatchOptions(this.getClass());

    var match = FuzzySearch
        .extractTop(target, typeMap.entrySet(), Entry::getKey, matchOptions.getLooseness());
    return match.stream().peek(m -> log.debug(
        "Score for " + m.getReferent().getKey() + " with term " + target + " was " + m
            .getScore())).filter(m -> m.getScore() >= matchOptions.getMinimumSimilarity())
        .map(m -> new MatcherResult<>(target, m.getScore(), m.getReferent().getValue()))
        .collect(toList());
  }
}
