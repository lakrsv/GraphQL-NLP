package com.github.lakrsv.graphql.nlp.schema.context;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.Map;

/**
 * {@inheritDoc}
 */
public class ContextMapperImpl implements ContextMapper {

  private final Map<String, TypeContext> typeContextsByTypeName;

  /**
   * Creates a {@link ContextMapper} with the supplied array of {@link TypeContext}s
   */
  public ContextMapperImpl(TypeContext[] typeContexts) {
    if (typeContexts == null || typeContexts.length == 0) {
      typeContextsByTypeName = Collections.emptyMap();
    } else {
      typeContextsByTypeName = stream(typeContexts)
          .collect(toMap(context -> context.getTypeName().toLowerCase(), context -> context));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ContextProcessor getContextProcessor(String typeName, String keyword) {
    if (typeContextsByTypeName.containsKey(typeName.toLowerCase())) {
      var context = typeContextsByTypeName.get(typeName.toLowerCase());
      var contextFiltersByKeyword = context.getContextFilterByKeyword();
      return contextFiltersByKeyword.getOrDefault(keyword.toLowerCase(), null);
    }
    return null;
  }
}
