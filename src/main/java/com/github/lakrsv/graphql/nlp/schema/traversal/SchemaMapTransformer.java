package com.github.lakrsv.graphql.nlp.schema.traversal;

import graphql.schema.GraphQLSchema;
import graphql.schema.TypeTraverser;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Transforms a supplied {@link GraphQLSchema} to an internal representation
 */
@RequiredArgsConstructor
public class SchemaMapTransformer {

  @NonNull
  private final String queryTypeName;
  @NonNull
  private final TypeTraverser typeTraverser;
  @NonNull
  private final GraphQLSchema schema;

  private Map<String, FieldInformation> graphQLTypeMaps;

  /**
   * The GraphQL schema represented as a map, where the key is a string representing the field name, and the value is
   * the {@link FieldInformation} representing the GraphQL field
   *
   * @return The GraphQL schema as a map
   */
  public Map<String, FieldInformation> lazyGetGraphQLSchemaMap() {
    if (graphQLTypeMaps != null) {
      return graphQLTypeMaps;
    }

    graphQLTypeMaps = new HashMap<>();

    schema.getType(queryTypeName).getChildren().forEach(t -> {
      var typeName = t.getName();
      var visitor = new GraphQLMapCollectingVisitor();
      typeTraverser.depthFirst(visitor, t);
      var result = visitor.getGraphQLTypeMap();

      if (result.containsKey(typeName)) {
        graphQLTypeMaps.put(typeName, result.get(typeName));
      }
    });

    return graphQLTypeMaps;
  }
}
