package com.github.lakrsv.graphql.nlp.exceptions;

/**
 * Exception thrown if query generation encountered issues with supplied arguments
 */
public class ArgumentException extends RuntimeException {

  public ArgumentException(String message) {
    super(message);
  }
}
