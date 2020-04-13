package com.temporalis.io.graphql.nlp.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ArgumentExceptionTest {

  @Test
  public void constructorHasExpectedMessage() {
    var expectedMessage = "Hello World";
    var argumentException = new ArgumentException(expectedMessage);
    assertEquals(expectedMessage, argumentException.getMessage());
  }
}
