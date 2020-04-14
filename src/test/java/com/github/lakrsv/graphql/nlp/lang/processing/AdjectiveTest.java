package com.github.lakrsv.graphql.nlp.lang.processing;

import static com.github.lakrsv.graphql.nlp.lang.processing.Tag.ADJECTIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class AdjectiveTest {

  private static final String EXPECTED_TOKEN = "token";
  private static final Tag EXPECTED_TAG = ADJECTIVE;

  @Test
  public void getTokenHasExpectedValue() {
    var adjective = new Adjective(EXPECTED_TOKEN, EXPECTED_TAG);
    assertEquals(EXPECTED_TOKEN, adjective.getToken());
  }

  @Test
  public void getTagHasExpectedValue() {
    var adjective = new Adjective(EXPECTED_TOKEN, EXPECTED_TAG);
    assertEquals(EXPECTED_TAG, adjective.getTag());
  }

  @Test
  public void constructorWithNullTokenThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> new Adjective(null, EXPECTED_TAG));
  }

  @Test
  public void constructorWithNullTagThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> new Adjective(EXPECTED_TOKEN, null));
  }
}
