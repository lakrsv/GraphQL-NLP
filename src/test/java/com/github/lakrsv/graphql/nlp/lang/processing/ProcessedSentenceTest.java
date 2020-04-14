package com.github.lakrsv.graphql.nlp.lang.processing;

import static com.github.lakrsv.graphql.nlp.lang.processing.Tag.COMPARATIVE_ADJECTIVE;
import static com.github.lakrsv.graphql.nlp.lang.processing.Tag.SINGULAR_OR_MASS_NOUN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.lakrsv.graphql.nlp.schema.argument.Argument;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProcessedSentenceTest {

  private static final String[] TOKENS = new String[]{"hello", "world"};
  private static final Tag[] TAGS = new Tag[]{COMPARATIVE_ADJECTIVE, SINGULAR_OR_MASS_NOUN};
  private static final List<ProcessedChunk> PROCESSED_CHUNKS = List.of(
      new ProcessedChunk("bla", List.of("no"), List.of(new Argument("ten", 10)), List.of()));

  private ProcessedSentence processedSentence;

  @BeforeEach
  public void setup() {
    processedSentence = new ProcessedSentence(TOKENS, TAGS, PROCESSED_CHUNKS);
  }

  @Test
  public void constructorWithNullTokensThrowsNullPointerException() {
    assertThrows(NullPointerException.class,
        () -> new ProcessedSentence(null, TAGS, PROCESSED_CHUNKS));
  }

  @Test
  public void constructorWithNullTagsThrowsNullPointerException() {
    assertThrows(NullPointerException.class,
        () -> new ProcessedSentence(TOKENS, null, PROCESSED_CHUNKS));
  }

  @Test
  public void constructorWithNullProcessedChunksThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> new ProcessedSentence(TOKENS, TAGS, null));
  }

  @Test
  public void getTokensReturnsExpectedTokens() {
    assertEquals(TOKENS, processedSentence.getTokens());
  }

  @Test
  public void getTagsReturnsExpectedTags() {
    assertEquals(TAGS, processedSentence.getTags());
  }

  @Test
  public void getProcessedChunksReturnsExpectedChunks() {
    assertEquals(PROCESSED_CHUNKS, processedSentence.getProcessedChunks());
  }
}
