package com.github.lakrsv.graphql.nlp.schema.context;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.github.lakrsv.graphql.nlp.schema.context.actions.ContextProcessorAction;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ContextProcessorTest {

  @Test
  public void getKeywordReturnsExpectedKeyword() {
    var expectedKeyword = "keyword";
    var contextProcessor = new ContextProcessor(expectedKeyword, emptyList());

    assertEquals(expectedKeyword, contextProcessor.getKeyword());
  }

  @Test
  public void getActionsReturnsExpectedActions() {
    var expectedActions = List.of(mock(ContextProcessorAction.class));
    var contextProcessor = new ContextProcessor("keyword", expectedActions);

    assertEquals(expectedActions, contextProcessor.getActions());
  }

  @Test
  public void constructorWithNullKeywordThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> new ContextProcessor(null, emptyList()));
  }

  @Test
  public void constructorWithNullActionsThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> new ContextProcessor("hello", null));
  }
}
