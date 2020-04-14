package com.github.lakrsv.graphql.nlp.query.result;

import static java.util.Comparator.comparingDouble;

import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The {@link QueryResult} contains all potential matching {@link SchemaResult}s and convenience methods to access them
 */
@RequiredArgsConstructor
public class QueryResult {

  /**
   * The potential matching {@link SchemaResult}s
   */
  @NonNull
  private final List<SchemaResult> schemaResults;

  /**
   * Gets the best matching {@link SchemaResult}
   *
   * @return The best matching {@link SchemaResult}, or {@link Optional#empty()} if it does not exist
   */
  public Optional<SchemaResult> getBestMatch() {
    return schemaResults.stream().max(comparingDouble(SchemaResult::getAverageScore));
  }

  /**
   * Gets all the potential matches
   *
   * @return A list of {@link SchemaResult} representing all matches
   */
  public List<SchemaResult> getAllMatches() {
    return schemaResults;
  }
}
