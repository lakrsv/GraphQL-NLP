package com.github.lakrsv.graphql.nlp.lang.processing;

import java.util.Map;
import java.util.Set;

/**
 * Responsible for the processing of user input
 */
public interface LanguageProcessor {

  /**
   * Convenience method for the return of the default {@link LanguageProcessor}
   *
   * @return The default {@link LanguageProcessor}
   */
  static LanguageProcessor DEFAULT() {
    return new LanguageProcessorImpl(new ArgumentProcessingFactoryImpl());
  }

  /**
   * Creates a {@link ProcessedSentence} from supplied user input
   *
   * @param sentence The sentence to process
   * @param synonyms Any synonyms to potential tokens in a sentence. If a synonym is detected, it will use the key. The
   * key of this {@link Map} is the object to substitute the synonym for. The values of this {@link Map} is a {@link
   * Set} of synonyms
   * @return A {@link ProcessedSentence} of the user input
   */
  ProcessedSentence process(String sentence, Map<String, Set<String>> synonyms);
}
