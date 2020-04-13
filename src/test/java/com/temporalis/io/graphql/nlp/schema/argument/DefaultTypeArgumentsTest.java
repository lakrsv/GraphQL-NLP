package com.temporalis.io.graphql.nlp.schema.argument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.temporalis.io.graphql.nlp.exceptions.ArgumentException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class DefaultTypeArgumentsTest {

  private static final String TYPE_NAME = "typeName";
  private static final Argument ARGUMENT_1 = new Argument("key1", 100);
  private static final Argument ARGUMENT_2 = new Argument("key2", "hello");

  private final DefaultTypeArguments defaultTypeArguments =
      new DefaultTypeArguments(TYPE_NAME, List.of(ARGUMENT_1, ARGUMENT_2));

  @Test
  public void constructorHasExpectedTypeName() {
    assertEquals(TYPE_NAME, defaultTypeArguments.getTypeName());
  }

  @Test
  public void constructorHasExpectedArgumentsByKey() {
    assertEquals(Map.of(ARGUMENT_1.getKey(), ARGUMENT_1.getValue(), ARGUMENT_2.getKey(),
        ARGUMENT_2.getValue()), defaultTypeArguments.getArgumentsByKey());
  }

  @Test
  public void constructorThrowsArgumentExceptionIfKeyIsNull() {
    assertThrows(ArgumentException.class,
        () -> new DefaultTypeArguments(TYPE_NAME, List.of(new Argument(null, "hello"))));
  }

  @Test
  public void constructorThrowsArgumentExceptionIfValueIsNull() {
    assertThrows(ArgumentException.class,
        () -> new DefaultTypeArguments(TYPE_NAME, List.of(new Argument("Hello", null))));
  }

  @Test
  public void constructorThrowsArgumentExceptionIfKeyIsEmpty() {
    assertThrows(ArgumentException.class,
        () -> new DefaultTypeArguments(TYPE_NAME, List.of(new Argument("", "hello"))));
  }
}
