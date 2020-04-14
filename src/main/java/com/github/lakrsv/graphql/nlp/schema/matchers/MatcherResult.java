package com.github.lakrsv.graphql.nlp.schema.matchers;

import java.util.ArrayList;
import java.util.List;
import lombok.Value;

/**
 * The result of a matching operation
 *
 * @param <T> The type to match
 */
@Value
public class MatcherResult<T> {

  /**
   * The term that created this match
   */
  private final String term;
  /**
   * The similarity of the result to the term
   */
  private final int similarity;
  /**
   * The result of the match operation
   */
  private final T result;
  /**
   * The inner path of the match, if is nested
   */
  private final List<T> innerPath = new ArrayList<>();
}
