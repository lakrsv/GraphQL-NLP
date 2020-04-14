package com.github.lakrsv.graphql.nlp.lang.processing;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A ProcessedSentence represents the user input after being natural language processing
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class ProcessedSentence {

  /**
   * The original tokens that created this {@link ProcessedSentence}
   */
  @NonNull
  private final String[] tokens;
  /**
   * The original tags that created this {@link ProcessedSentence}
   */
  @NonNull
  private final Tag[] tags;
  /**
   * A list of {@link ProcessedChunk} representing logical grouping of terms in the sentence
   */
  @NonNull
  private final List<ProcessedChunk> processedChunks;
}
