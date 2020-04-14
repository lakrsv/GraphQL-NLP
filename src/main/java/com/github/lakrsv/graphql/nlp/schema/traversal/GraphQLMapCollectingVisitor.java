package com.github.lakrsv.graphql.nlp.schema.traversal;

import static java.util.Collections.emptyMap;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeVisitorStub;
import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Visitor used during traversal of a GraphQL schema to collect information about it
 */
public class GraphQLMapCollectingVisitor extends GraphQLTypeVisitorStub {

  private Deque<Map<String, FieldInformation>> graphQLTypeMaps = new ArrayDeque<>() {{
    push(new HashMap<>());
  }};
  private GraphQLFieldDefinition previousField;
  private Deque<GraphQLFieldDefinition> visitedFields = new ArrayDeque<>();

  /**
   * @return The map representing the graphql schema, or an empty map if it does not exist
   */
  public Map<String, FieldInformation> getGraphQLTypeMap() {
    return graphQLTypeMaps.size() > 0 ? graphQLTypeMaps.getLast() : emptyMap();
  }

  /**
   * Visits a {@link GraphQLFieldDefinition} to collect information about it
   *
   * @param field The {@link GraphQLFieldDefinition} currently being visited
   * @param context The current {@link TraverserContext}
   * @return The traversal control, deciding whether the traversal algorithm should continue
   */
  @Override
  public TraversalControl visitGraphQLFieldDefinition(GraphQLFieldDefinition field,
      TraverserContext<GraphQLType> context) {

    var nodeParent = getParentNode(context);
    if (previousField != null && previousField != nodeParent) {
      graphQLTypeMaps.pop();
      visitedFields.pop();
      while (visitedFields.size() > 0 && visitedFields.peek() != nodeParent) {
        graphQLTypeMaps.pop();
        visitedFields.pop();
      }
    }
    var childMap = new HashMap<String, FieldInformation>();
    var newFieldInformation = new GraphQLFieldInformation(field, childMap);
    graphQLTypeMaps.peek().put(newFieldInformation.getName(), newFieldInformation);
    previousField = field;
    visitedFields.push(field);
    graphQLTypeMaps.push(childMap);
    return super.visitGraphQLFieldDefinition(field, context);
  }

  private GraphQLFieldDefinition getParentNode(TraverserContext<GraphQLType> context) {
    GraphQLFieldDefinition fieldDefinition = null;
    var nextContext = context.getParentContext();
    do {
      if (nextContext.thisNode() instanceof GraphQLFieldDefinition) {
        fieldDefinition = (GraphQLFieldDefinition) nextContext.thisNode();
      }
      nextContext = nextContext.getParentContext();
    } while (fieldDefinition == null && nextContext != null);
    return fieldDefinition;
  }
}
