package com.temporalis.io.graphql.nlp.schema.context;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import lombok.Value;

/**
 * The type contexts represent contextual actions to execute for a given type
 */
@Value
public class TypeContext {

  private final String typeName;
  private final Map<String, ContextProcessor> contextFilterByKeyword;

  /**
   * Creates a {@link TypeContext} for the given typeName with matching context processors
   *
   * @param typeName The type name to match
   * @param contextFilters The context processors for this type
   */
  public TypeContext(String typeName, ContextProcessor[] contextFilters) {
    this.typeName = typeName.toLowerCase();
    contextFilterByKeyword = stream(contextFilters)
        .collect(toMap(filter -> filter.getKeyword().toLowerCase(), filter -> filter));
  }
}
