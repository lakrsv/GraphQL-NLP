package com.temporalis.io.graphql.nlp.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ModelExceptionTest {

  @Test
  public void constructorHasExpectedMessage() {
    var expectedMessage = "Hello World";
    var modelException = new ModelException(expectedMessage, new Exception());
    assertEquals(expectedMessage, modelException.getMessage());
  }

  @Test
  public void constructorHasExpectedCause() {
    var expectedThrowable = new Exception("Hello World");
    var modelException = new ModelException("Hello World", expectedThrowable);
    assertEquals(expectedThrowable, modelException.getCause());
  }
}
