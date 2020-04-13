package com.temporalis.io.graphql.nlp.lang.processing;

import static com.temporalis.io.graphql.nlp.lang.processing.Tag.ADJECTIVE;
import static com.temporalis.io.graphql.nlp.lang.processing.Tag.COORDINATING_CONJUNCTION;
import static com.temporalis.io.graphql.nlp.lang.processing.Tag.DETERMINER;
import static com.temporalis.io.graphql.nlp.lang.processing.Tag.PAST_TENSE_VERB;
import static com.temporalis.io.graphql.nlp.lang.processing.Tag.PLURAL_NOUN;
import static com.temporalis.io.graphql.nlp.lang.processing.Tag.POSSESSIVE_PRONOUN;
import static com.temporalis.io.graphql.nlp.lang.processing.Tag.SINGULAR_OR_MASS_NOUN;
import static com.temporalis.io.graphql.nlp.lang.processing.Tag.THIRD_PERSON_SINGULAR_PRESENT_VERB;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.temporalis.io.graphql.nlp.schema.argument.Argument;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LanguageProcessorImplTest {

  private static final Map<String, Set<String>> TEST_SYNONYMS = Map.of("lars", Set.of("elvis"));
  private static final String TEST_SENTENCE =
      "My car is red, my house is blue, and elvis rode a bike last summer";
  private static final ProcessedSentence EXPECTED_PROCESSED_SENTENCE = new ProcessedSentence(
      TEST_SENTENCE.toLowerCase().replace(",", " ,").replace("elvis", "lars").split(" "),
      new Tag[]{POSSESSIVE_PRONOUN,
          SINGULAR_OR_MASS_NOUN,
          THIRD_PERSON_SINGULAR_PRESENT_VERB,
          ADJECTIVE,
          null,
          // commas can not be tagged
          POSSESSIVE_PRONOUN,
          SINGULAR_OR_MASS_NOUN,
          THIRD_PERSON_SINGULAR_PRESENT_VERB,
          ADJECTIVE,
          null,
          COORDINATING_CONJUNCTION,
          PLURAL_NOUN,
          PAST_TENSE_VERB,
          DETERMINER,
          SINGULAR_OR_MASS_NOUN,
          ADJECTIVE,
          SINGULAR_OR_MASS_NOUN}, new ArrayList<>() {{
    add(new ProcessedChunk("last summer", new ArrayList<>() {{
      add("summer");
    }}, new ArrayList<>(), new ArrayList<>() {{
      add(new Adjective("last", ADJECTIVE));
    }}));
    add(new ProcessedChunk("a bike", new ArrayList<>() {{
      add("bike");
    }}, new ArrayList<>(), new ArrayList<>()));
    add(new ProcessedChunk("lars", new ArrayList<>() {{
      add("lars");
    }}, new ArrayList<>(), new ArrayList<>()));
    add(new ProcessedChunk("my house", new ArrayList<>() {{
      add("house");
    }}, new ArrayList<>(), new ArrayList<>() {{
      add(new Adjective("blue", ADJECTIVE));
    }}));
    add(new ProcessedChunk("my car", new ArrayList<>() {{
      add("car");
    }}, new ArrayList<>(), new ArrayList<>() {{
      add(new Adjective("red", ADJECTIVE));
    }}));
  }});
  @Mock
  private TokenizerME tokenizerME;
  @Mock
  private POSTaggerME posTaggerME;
  @Mock
  private ChunkerME chunkerME;
  @Spy
  private ArgumentProcessingFactory argumentProcessingFactory =
      new ArgumentProcessingFactoryImpl();

  private LanguageProcessor languageProcessor;

  @BeforeEach
  public void setup() {
    languageProcessor = new LanguageProcessorImpl(argumentProcessingFactory);
  }


  @Test
  public void constructorAcceptsAndUsesModels() {
    new LanguageProcessorImpl(tokenizerME, posTaggerME, chunkerME, argumentProcessingFactory);
  }

  @Test
  public void defaultConstructorDoesNotThrow() {
    new LanguageProcessorImpl(argumentProcessingFactory);
  }

  @Test
  public void processReturnsExpectedProcessedSentence() {
    var actualProcessedSentence = languageProcessor.process(TEST_SENTENCE, TEST_SYNONYMS);
    assertEquals(EXPECTED_PROCESSED_SENTENCE, actualProcessedSentence);
  }

  @Test
  public void processNounsAreObjects() {
    var sentence = "My car";
    var expectedNounObject = "car";

    var processedSentence = languageProcessor.process(sentence, emptyMap());

    assertEquals(expectedNounObject,
        processedSentence.getProcessedChunks().get(0).getObjects().get(0));
  }

  @Test
  public void processWithAdjectiveBeforeNounHasAdjective() {
    var sentence = "My red and shiny car";
    var expectedAdjectives = new String[]{"red", "shiny"};

    var processedSentence = languageProcessor.process(sentence, emptyMap());

    assertArrayEquals(expectedAdjectives,
        processedSentence.getProcessedChunks().get(0).getAdjectives().stream()
            .map(Adjective::getToken).toArray());
  }

  @Test
  public void processWithAdjectiveAfterNounHasAdjective() {
    var sentence = "My car is red and shiny";
    var expectedAdjectives = new String[]{"red", "shiny"};

    var processedSentence = languageProcessor.process(sentence, emptyMap());

    assertArrayEquals(expectedAdjectives,
        processedSentence.getProcessedChunks().get(0).getAdjectives().stream()
            .map(Adjective::getToken).toArray());
  }

  @Test
  public void processWithArgumentBeforeNounHasArgument() {
    var sentence = "The first 100 cars";
    var expectedArgument = new Argument("first", "100");

    var processedSentence = languageProcessor.process(sentence, emptyMap());

    assertEquals(expectedArgument,
        processedSentence.getProcessedChunks().get(0).getArguments().get(0));
  }

  @Test
  public void processWithArgumentAfterNounHasArgument() {
    var sentence = "Cars that cost 1000 dollars";
    var expectedArgument = new Argument("cost", "1000");

    var processedSentence = languageProcessor.process(sentence, emptyMap());

    assertEquals(expectedArgument,
        processedSentence.getProcessedChunks().get(0).getArguments().get(0));
  }
}
