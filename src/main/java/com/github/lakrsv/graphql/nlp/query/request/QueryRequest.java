package com.github.lakrsv.graphql.nlp.query.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The query request representing the user input and other query options
 */
@Builder
@Getter
public class QueryRequest {

  /**
   * The user input string
   */
  @NonNull
  private final String text;
  /**
   * The query time options to modify how the query is executed
   */
  @NonNull
  @Builder.Default
  private final QueryRequestOptions options = QueryRequestOptions.DEFAULT;
}
