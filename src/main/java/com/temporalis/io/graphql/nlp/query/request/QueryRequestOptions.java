package com.temporalis.io.graphql.nlp.query.request;

import static java.util.Collections.emptyList;

import com.temporalis.io.graphql.nlp.schema.argument.DefaultTypeArguments;
import com.temporalis.io.graphql.nlp.schema.matchers.FieldMatcher;
import com.temporalis.io.graphql.nlp.schema.matchers.SeekingFieldMatcher;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * The query request options modify how a query is executed
 */
@Builder
@Getter
@EqualsAndHashCode
public class QueryRequestOptions {

  /**
   * The default {@link QueryRequestOptions}
   */
  public static final QueryRequestOptions DEFAULT = QueryRequestOptions.builder().build();

  // TODO - This breaks fuzziness
  /**
   * The first top-level field at which to start executing the query at
   */
  @Builder.Default
  private final String entryPoint = null;

  /**
   * Specific type arguments for the query
   */
  @Builder.Default
  private final List<DefaultTypeArguments> queryTypeArguments = emptyList();

  /**
   * The default match options for a {@link FieldMatcher}
   */
  @NonNull
  @Builder.Default
  private final MatchOptions defaultMatchOptions = MatchOptions.builder().build();
  /**
   * Optional specific {@link MatchOptions} for a specific {@link FieldMatcher}
   *
   * The key represents the type of the {@link FieldMatcher} that should use the specific options
   */
  @NonNull
  @Builder.Default
  private final Map<Type, MatchOptions> specificMatchOptions = Map.of(SeekingFieldMatcher.class,
      MatchOptions.builder().minimumSimilarity(80).looseness(2).build());
}
