package com.temporalis.io.graphql.nlp.exceptions;

/**
 * Exception thrown if machine-learning models failed loading
 */
public class ModelException extends RuntimeException {

  public ModelException(String message, Throwable t) {
    super(message, t);
  }
}
