package com.github.lakrsv.graphql.nlp.query;

import com.github.lakrsv.graphql.nlp.query.request.QueryRequest;
import com.github.lakrsv.graphql.nlp.query.result.QueryResult;

/**
 * Base interface for a query generator
 */
public interface QueryGenerator {

  /**
   * Uses a {@link QueryRequest} to create a {@link QueryResult} with matches from the user input
   *
   * @param queryRequest The query request to execute
   * @return The query result from the query
   */
  QueryResult convertToQuery(QueryRequest queryRequest);
}
