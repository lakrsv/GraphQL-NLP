package com.github.lakrsv.graphql.nlp.lang.processing;

import static com.github.lakrsv.graphql.nlp.lang.processing.Tag.CARDINAL_NUMBER;
import static com.github.lakrsv.graphql.nlp.lang.processing.Tag.isAdjective;

import com.github.lakrsv.graphql.nlp.schema.argument.Argument;
import lombok.NonNull;

/**
 * {@inheritDoc}
 * <p>
 * Default implementation of the {@link ArgumentProcessingFactory}
 */
public class ArgumentProcessingFactoryImpl implements ArgumentProcessingFactory {

  /**
   * {@inheritDoc}
   */
  @Override
  public Argument getArgument(@NonNull String[] tokens, @NonNull Tag[] tags,
      int currentTokenIndex) {
    if (currentTokenIndex < 0 || currentTokenIndex >= tokens.length
        || currentTokenIndex >= tags.length) {
      return null;
    }

    var currentToken = tokens[currentTokenIndex];
    var currentTag = tags[currentTokenIndex];
    if (CARDINAL_NUMBER == currentTag || previousTokenWasAdjective(tags,
        currentTokenIndex - 1)) {
      var potentialArgumentKey =
          (currentTokenIndex - 1) >= 0 ? tokens[currentTokenIndex - 1] : null;

      return new Argument(potentialArgumentKey, currentToken);
    }
    return null;
  }

  private boolean previousTokenWasAdjective(Tag[] tags, int index) {
    return index >= 0 && isAdjective((tags[index]));
  }
}
