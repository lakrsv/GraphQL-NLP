package com.temporalis.io.graphql.nlp.schema.matchers;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import com.temporalis.io.graphql.nlp.schema.traversal.FieldInformation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.xdrop.fuzzywuzzy.FuzzySearch;

/**
 * {@inheritDoc}
 * <p>
 * Performs matching on nested levels
 */
@Slf4j
@RequiredArgsConstructor
public class SeekingFieldMatcher implements FieldMatcher {

  /**
   * If bruteforcing, will try all levels of matching. If not bruteforcing, will return the first batch of valid matches
   * on the current level
   */
  private final boolean bruteforce;

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

    var queue = new ArrayDeque<TypeMapWithPathReference>();
    queue.push(new TypeMapWithPathReference(typeMap));

    Set<FieldInformation> previouslyVisited = new HashSet<>();

    List<List<MatcherResult<FieldInformation>>> validMatches = new ArrayList<>();
    while (!queue.isEmpty()) {

      var nextTypeMapWithPathReference = queue.pollFirst();
      if (nextTypeMapWithPathReference == null || nextTypeMapWithPathReference.typeMap == null
          || nextTypeMapWithPathReference.typeMap.isEmpty()) {
        continue;
      }
      var currentMatches = FuzzySearch
          .extractTop(target, nextTypeMapWithPathReference.typeMap.entrySet(),
              Map.Entry::getKey, matchOptions.getLooseness()).stream().peek(m -> log.debug(
              "Score for " + m.getReferent().getKey() + " with term " + target + " was " + m
                  .getScore()))
          .filter(m -> m.getScore() >= matchOptions.getMinimumSimilarity())
          .map(m -> new MatcherResult<>(target, m.getScore(), m.getReferent().getValue()))
          .collect(toList());

      if (!currentMatches.isEmpty()) {
        currentMatches.forEach(
            match -> match.getInnerPath().addAll(nextTypeMapWithPathReference.path));
        if (!bruteforce) {
          return currentMatches;
        } else {
          validMatches.add(currentMatches);
        }
      }

      for (var entry : nextTypeMapWithPathReference.typeMap.entrySet()) {
        if (previouslyVisited.contains(entry.getValue())) {
          continue;
        }
        previouslyVisited.add(entry.getValue());
        var child = new TypeMapWithPathReference(entry.getValue().getChildren());
        child.path.addAll(nextTypeMapWithPathReference.path);
        child.path.add(entry.getValue());
        queue.push(child);
      }
    }

    Comparator<MatcherResult<FieldInformation>> comparator =
        Comparator.comparingInt(MatcherResult::getSimilarity);
    Comparator<MatcherResult<FieldInformation>> reverseComparator = comparator.reversed();
    return validMatches.stream().flatMap(Collection::stream)
        .sorted(reverseComparator.thenComparingInt(MatcherResult::getSimilarity))
        .limit(matchOptions.getLooseness()).collect(toList());
  }


  @RequiredArgsConstructor
  @EqualsAndHashCode
  private class TypeMapWithPathReference {

    private final List<FieldInformation> path = new ArrayList<>();
    private final Map<String, FieldInformation> typeMap;
  }
}
