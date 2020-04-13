package com.temporalis.io.graphql.nlp.schema.context.actions;

import com.temporalis.io.graphql.nlp.query.result.GraphQLSchemaResult;
import graphql.ExecutionResult;

/**
 * Interface for the execution of contextual actions on a type
 */
public interface ContextProcessorAction {

  /**
   * The pre-processing step, executed if a {@link GraphQLSchemaResult} matches a contextual actions
   *
   * @param schemaResult The schema result that was matched
   * @return The {@link GraphQLSchemaResult} after modification (if any)
   */
  GraphQLSchemaResult preProcess(GraphQLSchemaResult schemaResult);

  /**
   * The post-processing step, which may be implemented by a service implementation.
   *
   * @param executionResult The {@link ExecutionResult} to post process
   * @return The {@link ExecutionResult} after modification (if any)
   */
  ExecutionResult postProcess(ExecutionResult executionResult);
}
