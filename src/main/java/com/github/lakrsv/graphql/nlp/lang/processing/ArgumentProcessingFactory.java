package com.github.lakrsv.graphql.nlp.lang.processing;

import com.github.lakrsv.graphql.nlp.schema.argument.Argument;

/**
 * Responsible for argument coercion for supplied tokens processed from user input
 */
public interface ArgumentProcessingFactory {

  /**
   * Gets an argument for a specific token, taking into account the other tokens in a {@link ProcessedChunk}
   *
   * @param tokens All the tokens in the chunk
   * @param tags All the tags for the tokens in the chunk
   * @param currentTokenIndex The index of the current token
   * @return An argument, or null if it could not be found
   */
  Argument getArgument(String[] tokens, Tag[] tags, int currentTokenIndex);
}
