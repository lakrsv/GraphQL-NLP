package com.github.lakrsv.graphql.nlp.schema.argument;

import lombok.Value;

/**
 * Represents an argument with a key and a value
 */
@Value
public class Argument {

  /**
   * The key of the argument
   */
  private final String key;
  /**
   * The value of the argument
   */
  private final Object value;
}
