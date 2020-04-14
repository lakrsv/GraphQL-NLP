package com.github.lakrsv.graphql.nlp.lang.processing;

import com.github.lakrsv.graphql.nlp.schema.argument.Argument;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * A ProcessedChunk represents a logical grouping of elements supplied in the user input.
 * <p>
 * It contains various fields which allow the library to perform type detection during schema traversal and query
 * generation
 */
@Value
@EqualsAndHashCode
public class ProcessedChunk {

  /**
   * The original string chunk which this {@link ProcessedChunk} represents
   */
  private final String originalChunk;
  /**
   * Potential objects detected to be present in the {@link ProcessedChunk}
   * <p>
   * These objects represent potential type and field matches that could be present in the schema
   */
  private final List<String> objects;
  /**
   * A list of {@link Argument}s which have been detected to be potentially related to the detected objects
   */
  private final List<Argument> arguments;
  /**
   * A list of {@link Adjective}s which have been detected to be present in the chunk, and which could act as modifiers
   * for contextual matching
   */
  private final List<Adjective> adjectives;
}
