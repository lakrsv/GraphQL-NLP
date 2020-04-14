package com.github.lakrsv.graphql.nlp.schema.traversal;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GraphQLFieldInformationTest {

  private static final String CHILD1_NAME = "child1";
  private static final String CHILD2_NAME = "child2";
  @Mock
  private GraphQLFieldDefinition graphQLFieldDefinition;

  @Mock
  private FieldInformation child1;
  @Mock
  private FieldInformation child2;

  private Map<String, FieldInformation> children;
  private GraphQLFieldInformation graphQLFieldInformation;

  @BeforeEach
  public void setup() {
    children = Map.of(CHILD1_NAME, child1, CHILD2_NAME, child2);
    graphQLFieldInformation = new GraphQLFieldInformation(graphQLFieldDefinition, children);
  }

  @Test
  public void constructorWithNullFieldDefinitionThrowsNullPointerException() {
    assertThrows(NullPointerException.class,
        () -> new GraphQLFieldInformation(null, emptyMap()));
  }

  @Test
  public void constructorWithNullChildrenThrowsNullPointerException() {
    assertThrows(NullPointerException.class,
        () -> new GraphQLFieldInformation(graphQLFieldDefinition, null));
  }

  @Test
  public void getNameReturnsFieldDefinitionName() {
    var expectedName = "name";
    when(graphQLFieldDefinition.getName()).thenReturn(expectedName);

    assertEquals(expectedName, graphQLFieldInformation.getName());
  }

  @Test
  public void hasChildReturnsTrueWhenChildExists() {
    assertTrue(graphQLFieldInformation.hasChild(CHILD1_NAME));
  }

  @Test
  public void hasChildReturnsFalseWhenChildDoesNotExist() {
    assertFalse(graphQLFieldInformation.hasChild("bllaskd"));
  }

  @Test
  public void getChildReturnsChild() {
    assertEquals(child1, graphQLFieldInformation.getChild(CHILD1_NAME));
  }

  @Test
  public void getInputArgumentsReturnsExpectedInputArguments() {
    var argument = mock(GraphQLArgument.class);
    when(argument.getName()).thenReturn("name");
    var arguments = List.of(argument);
    when(graphQLFieldDefinition.getArguments()).thenReturn(arguments);

    graphQLFieldInformation = new GraphQLFieldInformation(graphQLFieldDefinition, children);
    var inputArguments = graphQLFieldInformation.getInputArguments();

    Assertions.assertEquals(argument.getName(), inputArguments.get(0).getKey());
  }
}
