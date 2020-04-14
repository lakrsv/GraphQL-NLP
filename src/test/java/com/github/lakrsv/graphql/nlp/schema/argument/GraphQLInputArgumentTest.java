package com.github.lakrsv.graphql.nlp.schema.argument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import graphql.language.InputValueDefinition;
import graphql.language.NonNullType;
import graphql.language.Type;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLScalarType;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GraphQLInputArgumentTest {

  @Mock
  private GraphQLArgument graphQLArgument;

  private GraphQLInputArgument graphQLInputArgument;

  @BeforeEach
  public void setup() {
    when(graphQLArgument.getDefaultValue()).thenReturn("defaultValue");
    graphQLInputArgument = new GraphQLInputArgument(graphQLArgument);
  }

  @Test
  public void getKeyIsArgumentName() {
    var expectedName = "expectedName";
    when(graphQLArgument.getName()).thenReturn(expectedName);

    assertEquals(expectedName, graphQLInputArgument.getKey());
  }

  @Test
  public void isRequiredTrueWhenGraphQLArgumentIsNonNullType() {
    var nonNullInputValueDefinition = mock(InputValueDefinition.class);
    when(nonNullInputValueDefinition.getType()).thenReturn(new NonNullType(mock(Type.class)));
    when(graphQLArgument.getDefinition()).thenReturn(nonNullInputValueDefinition);

    assertTrue(graphQLInputArgument.isRequired());
  }

  @Test
  public void isRequiredIsFalseWhenGraphQLArgumentIsNotNonNullType() {
    var nullableInputValueDefinition = mock(InputValueDefinition.class, RETURNS_MOCKS);
    when(graphQLArgument.getDefinition()).thenReturn(nullableInputValueDefinition);

    assertFalse(graphQLInputArgument.isRequired());
  }

  @Test
  public void constructorWhenGraphQLArgumentIsNullThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> new GraphQLInputArgument(null));
  }

  @Test
  public void getValueReturnsGraphQLArgumentDefaultValueIfNotSet() {
    assertEquals("defaultValue", graphQLInputArgument.getValue());
  }

  @Test
  public void getValueReturnsSetValue() {
    var expectedValue = "hello world";

    graphQLInputArgument.setValue(expectedValue);

    assertEquals(expectedValue, graphQLInputArgument.getValue());
  }

  @Test
  public void getValueTypeReturnsExpectedTypeMapperClass() {
    var scalarName = "MyObject";
    var scalarTypeMapping = GraphQLInputArgumentTest.class;

    var scalarType = mock(GraphQLScalarType.class);
    when(graphQLArgument.getType()).thenReturn(scalarType);
    when(scalarType.getName()).thenReturn(scalarName);

    var valueType = graphQLInputArgument
        .getValueType(new GraphQLTypeMapper(Map.of(scalarName, scalarTypeMapping)));

    assertEquals(scalarTypeMapping, valueType.get());
  }
}
