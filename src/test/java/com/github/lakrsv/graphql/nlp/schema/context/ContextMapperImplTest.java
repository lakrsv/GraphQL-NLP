package com.github.lakrsv.graphql.nlp.schema.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.github.lakrsv.graphql.nlp.schema.context.actions.ContextProcessorAction;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ContextMapperImplTest {

  @Test
  public void constructorWithEmptyContextsDoesNotThrow() {
    new ContextMapperImpl(new TypeContext[0]);
  }

  @Test
  public void constructorWithNullContextsDoesNotThrow() {
    new ContextMapperImpl(null);
  }

  @Test
  public void getContextProcessorReturnsExpectedContextProcessor() {
    var expectedTypeName = "typeName";
    var contextProcessorOne = new ContextProcessor("contextProcessorOne",
        List.of(mock(ContextProcessorAction.class)));
    var contextProcessorTwo = new ContextProcessor("contextProcessorTwo",
        List.of(mock(ContextProcessorAction.class), mock(ContextProcessorAction.class)));
    var typeContextOne = new TypeContext(expectedTypeName,
        new ContextProcessor[]{contextProcessorOne, contextProcessorTwo});

    var contextMapper = new ContextMapperImpl(new TypeContext[]{typeContextOne});

    assertEquals(contextProcessorOne,
        contextMapper.getContextProcessor(expectedTypeName, contextProcessorOne.getKeyword()));
    assertEquals(contextProcessorTwo,
        contextMapper.getContextProcessor(expectedTypeName, contextProcessorTwo.getKeyword()));
  }
}
