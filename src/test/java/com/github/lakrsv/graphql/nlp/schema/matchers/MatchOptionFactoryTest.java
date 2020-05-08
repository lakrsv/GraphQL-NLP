package com.github.lakrsv.graphql.nlp.schema.matchers;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.lakrsv.graphql.nlp.query.request.MatchOptions;
import java.lang.reflect.Type;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MatchOptionFactoryTest {

  private static final MatchOptions DEFAULT_MATCH_OPTIONS =
      MatchOptions.builder().looseness(1).build();
  private static final MatchOptions EXPECTED_SPECIFIC_MATCH_OPTION =
      MatchOptions.builder().minimumSimilarity(1).build();
  private static final Map<Type, MatchOptions> SPECIFIC_MATCH_OPTIONS =
      Map.of(MatchOptionFactoryTest.class, EXPECTED_SPECIFIC_MATCH_OPTION);

  private MatchOptionFactory matchOptionFactory;

  @BeforeEach
  public void setup() {
    matchOptionFactory = new MatchOptionFactory(DEFAULT_MATCH_OPTIONS, SPECIFIC_MATCH_OPTIONS);
  }

  @Test
  public void constructorWithNullDefaultMatchOptionsThrowsNullPointerException() {
    assertThrows(NullPointerException.class,
        () -> new MatchOptionFactory(null, SPECIFIC_MATCH_OPTIONS));
  }

  @Test
  public void constructorWithNullSpecificMatchOptionsThrowsNullPointerException() {
    assertThrows(NullPointerException.class,
        () -> new MatchOptionFactory(DEFAULT_MATCH_OPTIONS, null));

  }

  @Test
  public void getDefaultMatchOptionsReturnsExpectedMatchOptions() {
    Assertions.assertEquals(DEFAULT_MATCH_OPTIONS, matchOptionFactory.getDefaultMatchOptions());
  }

  @Test
  public void getMatchOptionsReturnsExpectedMatchOptions() {
    Assertions.assertEquals(EXPECTED_SPECIFIC_MATCH_OPTION,
        matchOptionFactory.getMatchOptions(MatchOptionFactoryTest.class));
  }

  @Test
  public void getMatchOptionsReturnsDefaultMatchOptionsWhenNotPresent() {
    Assertions.assertEquals(DEFAULT_MATCH_OPTIONS, matchOptionFactory.getMatchOptions(String.class));
  }
}
