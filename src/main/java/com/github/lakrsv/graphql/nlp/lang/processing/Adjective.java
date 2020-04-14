package com.github.lakrsv.graphql.nlp.lang.processing;

import lombok.NonNull;
import lombok.Value;

/**
 * An adjective represents a potential modifier to objects in a {@link ProcessedChunk}
 */
@Value
class Adjective {

  @NonNull
  private final String token;
  @NonNull
  private final Tag tag;
}
