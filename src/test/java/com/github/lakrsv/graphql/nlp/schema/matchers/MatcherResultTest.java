package com.github.lakrsv.graphql.nlp.schema.matchers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MatcherResultTest {

  private static final String TERM = "term";
  private static final int SIMILARITY = 100;
  private static final String RESULT = "result";
  private static final List<String> INNER_PATH = List.of("inner", "path");

  private MatcherResult<String> result;

  @BeforeEach
  public void setup() {
    result = new MatcherResult<>(TERM, SIMILARITY, RESULT);
    result.getInnerPath().addAll(INNER_PATH);
  }

  @Test
  public void getTermReturnsExpectedTerm() {
    assertEquals(TERM, result.getTerm());
  }

  @Test
  public void getSimilarityReturnsExpectedSimilarity() {
    assertEquals(SIMILARITY, result.getSimilarity());
  }

  @Test
  public void getResultReturnsExpectedResult() {
    assertEquals(RESULT, result.getResult());
  }

  @Test
  public void getInnerPathReturnsExpectedInnerPath() {
    assertEquals(INNER_PATH, result.getInnerPath());
  }
}
