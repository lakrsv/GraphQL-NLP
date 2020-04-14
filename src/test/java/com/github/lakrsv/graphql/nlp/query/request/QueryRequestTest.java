package com.github.lakrsv.graphql.nlp.query.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class QueryRequestTest {

  @Test
  public void getTextReturnsExpectedText() {
    var expectedText = "Hello, beautiful world!";
    var request = QueryRequest.builder().text(expectedText).build();
    assertEquals(expectedText, request.getText());
  }

  @Test
  public void getOptionsReturnsExpectedOptions() {
    var expectedOptions = QueryRequestOptions.builder().entryPoint("hello").build();
    var request = QueryRequest.builder().text("hello").options(expectedOptions).build();
    assertEquals(expectedOptions, request.getOptions());
  }

  @Test
  public void buildQueryRequestWithNullTextThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> QueryRequest.builder().text(null).build());
  }

  @Test
  public void buildQueryRequestWithNullQueryRequestOptionsThrowsNullPointerException() {
    assertThrows(NullPointerException.class,
        () -> QueryRequest.builder().text("hello").options(null).build());
  }
}
