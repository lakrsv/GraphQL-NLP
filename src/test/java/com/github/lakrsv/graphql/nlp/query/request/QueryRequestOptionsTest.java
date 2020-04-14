package com.github.lakrsv.graphql.nlp.query.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.lakrsv.graphql.nlp.schema.matchers.KeyScoreFieldMatcher;
import java.lang.reflect.Type;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class QueryRequestOptionsTest {

  @Test
  public void getDefaultQueryOptionsReturnsDefaultQueryRequestOptions() {
    assertEquals(QueryRequestOptions.builder().build(), QueryRequestOptions.DEFAULT);
  }

  @Test
  public void getEntryPointReturnsExpectedEntrypoint() {
    var expectedEntryPoint = "llama";
    var options = QueryRequestOptions.builder().entryPoint(expectedEntryPoint).build();
    assertEquals(expectedEntryPoint, options.getEntryPoint());
  }

  @Test
  public void getDefaultMatchOptionsReturnsExpectedMatchOptions() {
    var expectedMatchOptions = MatchOptions.builder().looseness(5).build();
    var options =
        QueryRequestOptions.builder().defaultMatchOptions(expectedMatchOptions).build();
    assertEquals(expectedMatchOptions, options.getDefaultMatchOptions());
  }

  @Test
  public void getSpecificMatchOptionsReturnsExpectedSpecificMatchOptions() {
    Map<Type, MatchOptions> expectedSpecificMatchOptions = Map.of(KeyScoreFieldMatcher.class,
        MatchOptions.builder().looseness(5).minimumSimilarity(10).build());
    var options =
        QueryRequestOptions.builder().specificMatchOptions(expectedSpecificMatchOptions)
            .build();
    assertEquals(expectedSpecificMatchOptions, options.getSpecificMatchOptions());
  }
}
