package com.github.lakrsv.graphql.nlp.query.request;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Runtime options to modify how terms in queries are matched
 */
@Builder
@Getter
@EqualsAndHashCode
public class MatchOptions {

  /**
   * The minimum similarity required for a term to get successfully matched, in percentage
   */
  @Builder.Default
  private final int minimumSimilarity = 69;
  /**
   * The minimum looseness. This determines how many potential matches may be returned
   * <p>
   * A higher value means the queries generated from user input will be larger, targeting a broader range of potential
   * terms
   */
  @Builder.Default
  private final int looseness = 5;
}
