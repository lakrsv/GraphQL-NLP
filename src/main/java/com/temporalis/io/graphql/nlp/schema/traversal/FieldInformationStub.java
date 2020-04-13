package com.temporalis.io.graphql.nlp.schema.traversal;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import com.temporalis.io.graphql.nlp.schema.argument.InputArgument;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;

/**
 * {@inheritDoc}
 */
@EqualsAndHashCode
public abstract class FieldInformationStub implements FieldInformation {

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<InputArgument> getInputArguments() {
    return emptyList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<InputArgument> getRequiredArgumentsWithMissingValues() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasChild(String name) {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FieldInformation getChild(String name) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, FieldInformation> getChildren() {
    return emptyMap();
  }
}
