package com.temporalis.io.graphql.nlp.schema.argument;

/**
 * Represents an input argument in a query
 */
public interface InputArgument {

  /**
   * @return The key of the argument
   */
  String getKey();

  /**
   * @return Whether the argument is required
   */
  boolean isRequired();

  /**
   * @return The value of the argument
   */
  Object getValue();

  /**
   * Sets the value of the argument
   *
   * @param value The value to set
   */
  void setValue(Object value);

}
