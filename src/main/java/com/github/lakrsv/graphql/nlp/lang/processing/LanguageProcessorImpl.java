package com.github.lakrsv.graphql.nlp.lang.processing;

import static com.github.lakrsv.graphql.nlp.lang.processing.Tag.isAdjective;
import static com.github.lakrsv.graphql.nlp.lang.processing.Tag.isNoun;
import static com.github.lakrsv.graphql.nlp.lang.processing.Tag.isPronoun;

import com.github.lakrsv.graphql.nlp.exceptions.ModelException;
import com.github.lakrsv.graphql.nlp.schema.argument.Argument;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.jetbrains.annotations.NotNull;

/**
 * {@inheritDoc}
 */
@RequiredArgsConstructor
@Slf4j
public class LanguageProcessorImpl implements LanguageProcessor {

  @NonNull
  private final TokenizerME tokenizer;
  @NonNull
  private final POSTaggerME posTagger;
  @NonNull
  private final ChunkerME chunker;
  @NonNull
  private final ArgumentProcessingFactory argumentProcessingFactory;

  /**
   * The default constructor of the {@link LanguageProcessorImpl}
   * <p>
   * It creates a LanguageProcessor with trained machine-learning models which are built in to the library
   *
   * @param argumentProcessingFactory The argument processing factory to use in the {@link LanguageProcessor}
   */
  public LanguageProcessorImpl(@NonNull ArgumentProcessingFactory argumentProcessingFactory) {
    this.argumentProcessingFactory = argumentProcessingFactory;
    try {
      tokenizer = new TokenizerME(new TokenizerModel(
          LanguageProcessor.class.getResourceAsStream("/nlp/models/en-token.bin")));
      posTagger = new POSTaggerME(new POSModel(
          LanguageProcessor.class.getResourceAsStream("/nlp/models/en-pos-maxent.bin")));
      chunker = new ChunkerME(new ChunkerModel(
          LanguageProcessor.class.getResourceAsStream("/nlp/models/en-chunker.bin")));
    } catch (IOException e) {
      throw new ModelException("Failed loading Models", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ProcessedSentence process(String sentence, Map<String, Set<String>> synonyms) {

    String[] tokens = getTokens(sentence, synonyms);

    var stringTags = posTagger.tag(tokens);
    var chunkSpans = chunker.chunkAsSpans(tokens, stringTags);
    var chunkStrings = Span.spansToStrings(chunkSpans, tokens);

    var tags = Arrays.stream(stringTags).map(Tag::fromString).toArray(Tag[]::new);

    List<ProcessedChunk> processedChunks =
        createProcessedChunks(tokens, tags, chunkSpans, chunkStrings);

    return new ProcessedSentence(tokens, tags, processedChunks);
  }

  @NotNull
  private List<ProcessedChunk> createProcessedChunks(String[] tokens, Tag[] tags,
      Span[] chunkSpans, String[] chunkStrings) {
    var orphanedArguments = new ArrayList<Argument>();
    var orphanedAdjectives = new ArrayList<Adjective>();

    List<ProcessedChunk> processedChunks = new ArrayList<>();
    for (var i = chunkSpans.length - 1; i >= 0; --i) {
      var chunkSpan = chunkSpans[i];

      var nouns = new ArrayList<String>();
      var arguments = new ArrayList<Argument>();
      var adjectives = new ArrayList<Adjective>();

      if (!orphanedArguments.isEmpty()) {
        arguments.addAll(orphanedArguments);
        orphanedArguments.clear();
      }
      if (!orphanedAdjectives.isEmpty()) {
        adjectives.addAll(orphanedAdjectives);
        orphanedAdjectives.clear();
      }

      for (var j = chunkSpan.getStart(); j < chunkSpan.getEnd(); ++j) {
        var tag = tags[j];
        var token = tokens[j];

        if (tag == null) {
          continue;
        }
        if (isPronoun(tag)) {
          continue;
        }

        if (isAdjective(tag)) {
          adjectives.add(new Adjective(token, tag));
        }

        Argument argument;
        if (isNoun(tag)) {
          nouns.add(token);
        } else if ((argument = argumentProcessingFactory.getArgument(tokens, tags, j))
            != null) {
          arguments.add(argument);
        }
      }
      if (nouns.isEmpty()) {
        if (!arguments.isEmpty()) {
          orphanedArguments.addAll(arguments);
        }
        if (!adjectives.isEmpty()) {
          orphanedAdjectives.addAll(adjectives);
        }
        continue;
      }
      processedChunks.add(new ProcessedChunk(chunkStrings[i], nouns, arguments, adjectives));
    }
    return processedChunks;
  }

  @NotNull
  private String[] getTokens(String sentence, Map<String, Set<String>> synonyms) {
    return Arrays.stream(tokenizer.tokenize(sentence)).map(token -> {
      for (var synonym : synonyms.entrySet()) {
        if (synonym.getValue().contains(token)) {
          return synonym.getKey().toLowerCase();
        }
      }
      return token.toLowerCase();
    }).toArray(String[]::new);
  }
}
