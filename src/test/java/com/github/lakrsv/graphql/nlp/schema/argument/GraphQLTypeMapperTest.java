package com.github.lakrsv.graphql.nlp.schema.argument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import graphql.schema.GraphQLScalarType;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class GraphQLTypeMapperTest {

  @Test
  public void constructorWhenTypeMapIsNullThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> new GraphQLTypeMapper(null));
  }

  @Test
  public void getArgumentTypeClassReturnsExpectedMappedType() {
    var typeMapper = new GraphQLTypeMapper(Map.of("hello", GraphQLTypeMapperTest.class));
    var inputType = mock(GraphQLScalarType.class);
    when(inputType.getName()).thenReturn("hello");

    assertEquals(GraphQLTypeMapperTest.class, typeMapper.getArgumentTypeClass(inputType).get());
  }

  @Test
  public void getArgumentTypeClassReturnsEmptyOptionalIfNoMatch() {
    var typeMapper = new GraphQLTypeMapper(Map.of("hello", GraphQLTypeMapperTest.class));
    var inputType = mock(GraphQLScalarType.class);
    when(inputType.getName()).thenReturn("nothello");

    assertTrue(typeMapper.getArgumentTypeClass(inputType).isEmpty());
  }
}
