package com.temporalis.io.graphql.nlp.schema.context;

/**
 * Executes contextual actions based on specific keywords supplied for a specific type
 */
public interface ContextMapper {

  /**
   * Returns the {@link ContextProcessor} to use for a specific set of inputs
   *
   * @param typeName The typename to find the context processor for
   * @param keyword The keyword to find the context processor for
   * @return The context processor to use, or null if it doesn't exist
   */
  ContextProcessor getContextProcessor(String typeName, String keyword);
}
