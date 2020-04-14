package com.github.lakrsv.graphql.nlp.schema.matchers;

import com.github.lakrsv.graphql.nlp.query.request.MatchOptions;
import java.lang.reflect.Type;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Factory for {@link MatchOptions} for a specific type
 */
@RequiredArgsConstructor
public class MatchOptionFactory {

  /**
   * The default {@link MatchOptions} to use if there are no specific match options for a given type
   */
  @NonNull
  @Getter
  private final MatchOptions defaultMatchOptions;
  /**
   * Specific {@link MatchOptions} to use for a given type
   */
  @NonNull
  private final Map<Type, MatchOptions> specificMatchOptions;

  /**
   * Gets specific match options for a given type
   *
   * @param type The type to get match options for
   * @return Specific match options, or default match options if not specified
   */
  public MatchOptions getMatchOptions(Type type) {
    return specificMatchOptions.getOrDefault(type, defaultMatchOptions);
  }
}
