package com.github.lakrsv.graphql.nlp.schema.context;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.github.lakrsv.graphql.nlp.schema.context.actions.ContextProcessorAction;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TypeContextTest {

  @Test
  public void getTypeNameReturnsExpectedTypeNameAsLowerCase() {
    var expectedTypeName = "typeName";
    var typeContext = new TypeContext(expectedTypeName, new ContextProcessor[0]);
    assertEquals(expectedTypeName.toLowerCase(), typeContext.getTypeName());
  }

  @Test
  public void getContextFilterByKeywordReturnsExpectedContextProcessors() {

    var contextProcessorOne =
        new ContextProcessor("keyword1", List.of(mock(ContextProcessorAction.class)));
    var contextProcessorTwo = new ContextProcessor("keyword2", emptyList());
    var contextProcessors = new ContextProcessor[]{contextProcessorOne, contextProcessorTwo};

    var typeContext = new TypeContext("typeName", contextProcessors);
    var contextFilterByKeyword = typeContext.getContextFilterByKeyword();

    assertEquals(contextProcessorOne,
        contextFilterByKeyword.get(contextProcessorOne.getKeyword()));
    assertEquals(contextProcessorTwo,
        contextFilterByKeyword.get(contextProcessorTwo.getKeyword()));
  }
}
