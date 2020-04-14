package com.github.lakrsv.graphql.nlp.schema.traversal;

import static java.util.stream.Collectors.toList;

import com.github.lakrsv.graphql.nlp.schema.argument.GraphQLInputArgument;
import com.github.lakrsv.graphql.nlp.schema.argument.InputArgument;
import graphql.schema.GraphQLFieldDefinition;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * {@inheritDoc}
 */
@EqualsAndHashCode
public class GraphQLFieldInformation implements FieldInformation {

  @NonNull
  private final GraphQLFieldDefinition fieldDefinition;
  @NonNull
  private final Map<String, FieldInformation> children;
  @NonNull
  private final List<InputArgument> inputArguments;

  public GraphQLFieldInformation(@NonNull GraphQLFieldDefinition fieldDefinition,
      @NonNull Map<String, FieldInformation> children) {
    this.fieldDefinition = fieldDefinition;
    this.children = children;

    inputArguments = fieldDefinition.getArguments().stream().map(GraphQLInputArgument::new)
        .collect(toList());
  }

  /**
   * {@inheritDoc}
   */
  public String getName() {
    return fieldDefinition.getName();
  }

  /**
   * {@inheritDoc}
   */
  public boolean hasChild(String name) {
    return children.containsKey(name);
  }

  /**
   * {@inheritDoc}
   */
  public FieldInformation getChild(String name) {
    return children.get(name);
  }

  /**
   * {@inheritDoc}
   */
  public Map<String, FieldInformation> getChildren() {
    return children;
  }

  /**
   * {@inheritDoc}
   */
  public List<InputArgument> getInputArguments() {
    return inputArguments;
  }

  /**
   * {@inheritDoc}
   */
  public List<InputArgument> getRequiredArgumentsWithMissingValues() {
    return inputArguments.stream()
        .filter(InputArgument::isRequired)
        .filter(arg -> arg.getValue() == null)
        .collect(toList());
  }

  @Override
  public String toString() {
    var typeText = "Type: \t\t" + fieldDefinition.getName();
    //var childText = children.size() > 0 ? "\nChildren: \t" + String.join(",", children.keySet()) : "";
    return typeText;
  }
}
