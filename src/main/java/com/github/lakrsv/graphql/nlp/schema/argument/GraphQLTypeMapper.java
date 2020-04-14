package com.github.lakrsv.graphql.nlp.schema.argument;

import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLScalarType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;

/**
 * Responsible for converting a GraphQL type to the Java equivalent
 */
public class GraphQLTypeMapper {

  public static final Map<String, Class> DEFAULT_TYPE_MAP = new HashMap<>() {{
    put("String", String.class);
    put("Int", Integer.class);
  }};

  /**
   * The default {@link GraphQLTypeMapper}
   */
  public static final GraphQLTypeMapper DEFAULT_TYPE_MAPPER =
      new GraphQLTypeMapper(DEFAULT_TYPE_MAP);

  private final Map<String, Class> typeMap;

  /**
   * The default constructor of the type mapper
   *
   * @param typeMap The typemap to use, where the key is a string value of the type name, and the value is the Java
   * class equivalent
   */
  public GraphQLTypeMapper(@NonNull Map<String, Class> typeMap) {
    this.typeMap = typeMap;
  }

  /**
   * Get the java class type from a {@link GraphQLInputType}
   *
   * @param graphQLInputType The input type to get the corresponding Java type from
   * @return The equivalent java type, or {@link Optional#empty()} if it doesn't exist
   */
  public Optional<Class> getArgumentTypeClass(GraphQLInputType graphQLInputType) {
    // TODO - Handle non-scalar types
    if (graphQLInputType instanceof GraphQLScalarType) {
      var scalarType = (GraphQLScalarType) graphQLInputType;
      var scalarTypeName = scalarType.getName();
      return Optional.ofNullable(typeMap.getOrDefault(scalarTypeName, null));
    }
    return Optional.empty();
  }
}
