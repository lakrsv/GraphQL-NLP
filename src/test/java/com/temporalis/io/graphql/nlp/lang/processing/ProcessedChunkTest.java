package com.temporalis.io.graphql.nlp.lang.processing;

import static com.temporalis.io.graphql.nlp.lang.processing.Tag.ADJECTIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.temporalis.io.graphql.nlp.schema.argument.Argument;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProcessedChunkTest {

  private static final String ORIGINAL_CHUNK = "originalChunk";
  private static final List<String> OBJECTS = List.of("object1", "object2");
  private static final List<Argument> ARGUMENTS =
      List.of(new Argument("hello", "world"), new Argument("ten", 1000));
  private static final List<Adjective> ADJECTIVES =
      List.of(new Adjective("ten", ADJECTIVE), new Adjective("ton", ADJECTIVE));

  private ProcessedChunk processedChunk;

  @BeforeEach
  public void setup() {
    processedChunk = new ProcessedChunk(ORIGINAL_CHUNK, OBJECTS, ARGUMENTS, ADJECTIVES);
  }

  @Test
  public void getOriginalChunkReturnsExpected() {
    assertEquals(ORIGINAL_CHUNK, processedChunk.getOriginalChunk());
  }

  @Test
  public void getObjectsReturnsExpected() {
    assertEquals(OBJECTS, processedChunk.getObjects());
  }

  @Test
  public void getArgumentsReturnsExpected() {
    assertEquals(ARGUMENTS, processedChunk.getArguments());
  }

  @Test
  public void getAdjectivesReturnsExpected() {
    assertEquals(ADJECTIVES, processedChunk.getAdjectives());
  }
}
