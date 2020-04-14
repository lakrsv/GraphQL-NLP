package com.github.lakrsv.graphql.nlp.query.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MatchOptionsTest {

  @Test
  public void getMinimumSimilarityReturnsExpectedSimilarity() {
    var expectedSimilarity = 5;
    var matchOptions = MatchOptions.builder().minimumSimilarity(expectedSimilarity).build();
    assertEquals(expectedSimilarity, matchOptions.getMinimumSimilarity());
  }

  @Test
  public void getLoosenessReturnsExpectedLooseness() {
    var expectedLooseness = 10;
    var matchOptions = MatchOptions.builder().looseness(expectedLooseness).build();
    assertEquals(expectedLooseness, matchOptions.getLooseness());
  }
}
