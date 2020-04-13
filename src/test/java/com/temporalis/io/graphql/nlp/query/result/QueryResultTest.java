package com.temporalis.io.graphql.nlp.query.result;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;

public class QueryResultTest {

  @Test
  public void constructorWithNullSchemaResultsThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> new QueryResult(null));
  }

  @Test
  public void getAllMatchesReturnsExpectedResults() {
    var expectedResults = List.of(mock(SchemaResult.class));
    var result = new QueryResult(expectedResults);

    assertEquals(expectedResults, result.getAllMatches());
  }

  @Test
  public void getBestMatchReturnsSchemaResultWithHighestAverageScore() {
    var highestScoringResult = mock(SchemaResult.class);
    when(highestScoringResult.getAverageScore()).thenReturn(100f);
    var lowestScoringResult = mock(SchemaResult.class);
    when(lowestScoringResult.getAverageScore()).thenReturn(99f);

    var result = new QueryResult(List.of(highestScoringResult, lowestScoringResult));

    assertEquals(highestScoringResult, result.getBestMatch().get());
  }

  @Test
  public void getBestMatchReturnsEmptyWhenNoMatchesPresent() {
    var result = new QueryResult(emptyList());

    assertTrue(result.getBestMatch().isEmpty());
  }
}
