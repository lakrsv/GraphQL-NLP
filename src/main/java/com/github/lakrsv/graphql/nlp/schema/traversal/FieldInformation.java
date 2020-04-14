package com.github.lakrsv.graphql.nlp.schema.traversal;

import com.github.lakrsv.graphql.nlp.schema.argument.InputArgument;
import java.util.List;
import java.util.Map;

/**
 * Contains information about a specific field
 */
public interface FieldInformation {

  /**
   * @return The name of the field
   */
  String getName();

  /**
   * Check if a child exists
   *
   * @param name The name of the child
   * @return Whether the child exists
   */
  boolean hasChild(String name);

  /**
   * Gets a named child
   *
   * @param name The name of the child
   * @return The child {@link FieldInformation}, or null if it does not exist
   */
  FieldInformation getChild(String name);

  /**
   * @return All the children of the field
   */
  Map<String, FieldInformation> getChildren();

  /**
   * @return The {@link InputArgument}s on the field
   */
  List<InputArgument> getInputArguments();

  /**
   * @return All required {@link InputArgument}s with missing values
   */
  List<InputArgument> getRequiredArgumentsWithMissingValues();
}
