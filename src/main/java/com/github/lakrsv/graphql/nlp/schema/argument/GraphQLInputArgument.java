package com.github.lakrsv.graphql.nlp.schema.argument;

import graphql.language.NonNullType;
import graphql.schema.GraphQLArgument;
import java.util.Optional;
import lombok.NonNull;

/**
 * {@inheritDoc}
 */
public class GraphQLInputArgument implements InputArgument {

  private final GraphQLArgument graphQLArgument;
  private Object value;

  public GraphQLInputArgument(@NonNull GraphQLArgument graphQLArgument) {
    this.graphQLArgument = graphQLArgument;
    this.value = graphQLArgument.getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getKey() {
    return graphQLArgument.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isRequired() {
    return graphQLArgument.getDefinition().getType() instanceof NonNullType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getValue() {
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setValue(Object value) {
    // TODO - Type validation
    this.value = value;
  }

  public Optional<Class> getValueType(GraphQLTypeMapper typeMapper) {
    return typeMapper.getArgumentTypeClass(graphQLArgument.getType());
  }
}
