package com.github.lakrsv.graphql.nlp.schema.argument;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ArgumentTest {

  @Test
  public void constructorHasExpectedKey() {
    var expectedKey = "key";
    var argument = new Argument(expectedKey, "value");
    assertEquals(expectedKey, argument.getKey());
  }

  @Test
  public void constructorHasExpectedValue() {
    var expectedValue = "value";
    var argument = new Argument("key", expectedValue);
    assertEquals(expectedValue, argument.getValue());
  }
}
