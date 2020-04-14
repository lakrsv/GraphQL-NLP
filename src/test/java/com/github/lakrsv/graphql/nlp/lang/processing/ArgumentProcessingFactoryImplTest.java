package com.github.lakrsv.graphql.nlp.lang.processing;

import static com.github.lakrsv.graphql.nlp.lang.processing.Tag.ADJECTIVE;
import static com.github.lakrsv.graphql.nlp.lang.processing.Tag.CARDINAL_NUMBER;
import static com.github.lakrsv.graphql.nlp.lang.processing.Tag.PROPER_PLURAL_NOUN;
import static graphql.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ArgumentProcessingFactoryImplTest {

  private static final String[] TWO_TOKENS = new String[]{"Hello", "World"};
  private static final Tag[] TWO_TAGS = new Tag[]{CARDINAL_NUMBER, CARDINAL_NUMBER};
  private static final String[] EMPTY_TOKENS = new String[0];
  private static final Tag[] EMPTY_TAGS = new Tag[0];

  private static final ArgumentProcessingFactory argumentProcessingFactory =
      new ArgumentProcessingFactoryImpl();

  @Test
  public void getArgumentWithNullTokensThrowsNullPointerException() {
    assertThrows(NullPointerException.class,
        () -> argumentProcessingFactory.getArgument(null, EMPTY_TAGS, 0));
  }

  @Test
  public void getArgumentWithNullTagsThrowsNullPointerException() {
    assertThrows(NullPointerException.class,
        () -> argumentProcessingFactory.getArgument(EMPTY_TOKENS, null, 0));
  }

  @Test
  public void getArgumentReturnsArgumentWhenTagIsCardinalNumber() {

    var argument = argumentProcessingFactory.getArgument(TWO_TOKENS, TWO_TAGS, 0);

    assertNotNull(argument);
  }

  @Test
  public void getArgumentHasTokenAtTokenIndexAsValue() {
    var tokenIndex = 0;
    var expectedValue = TWO_TOKENS[tokenIndex];

    var argument =
        argumentProcessingFactory.getArgument(TWO_TOKENS, TWO_TAGS, tokenIndex);
    var actualValue = argument.getValue();

    assertEquals(expectedValue, actualValue);
  }

  @Test
  public void getArgumentHasKeyAtTokenIndexMinus1AsKey() {
    var tokenIndex = 1;
    var expectedKey = TWO_TOKENS[tokenIndex - 1];

    var argument =
        argumentProcessingFactory.getArgument(TWO_TOKENS, TWO_TAGS, tokenIndex);
    var actualKey = argument.getKey();

    assertEquals(expectedKey, actualKey);
  }

  @Test
  public void getArgumentHasArgumentWhenPreviousTokenWasAdjective() {
    var tags = new Tag[]{ADJECTIVE, PROPER_PLURAL_NOUN};
    var tokens = new String[]{"expectedKey", "value"};
    var currentTokenIndex = 1;

    var argument = argumentProcessingFactory.getArgument(tokens, tags, currentTokenIndex);

    assertNotNull(argument);
  }

  @Test
  public void getArgumentWhenCurrentTokenIndexIsLessThan0ReturnsNull() {
    var argument = argumentProcessingFactory.getArgument(TWO_TOKENS, TWO_TAGS, -1);

    assertNull(argument);
  }

  @Test
  public void getArgumentWhenCurrentTokenIndexIsGreaterThanTagsArrayLengthReturnsNull() {
    var tags = new Tag[]{ADJECTIVE, ADJECTIVE, ADJECTIVE};
    var tokens = new String[]{"Hello", "World", "Hello", "World", "Hello"};

    var argument = argumentProcessingFactory.getArgument(tokens, tags, 4);

    assertNull(argument);
  }

  @Test
  public void getArgumentWhenCurrentTokenIndexIsEqualToTagsArrayLengthReturnsNull() {
    var tags = new Tag[]{ADJECTIVE, ADJECTIVE, ADJECTIVE};
    var tokens = new String[]{"Hello", "World", "Hello", "World"};

    var argument = argumentProcessingFactory.getArgument(tokens, tags, 3);

    assertNull(argument);
  }

  @Test
  public void getArgumentWhenCurrentTokenIndexIsGreaterThanTokenArrayLengthReturnsNull() {
    var tags = new Tag[]{ADJECTIVE, ADJECTIVE, ADJECTIVE, ADJECTIVE};
    var tokens = new String[]{"Hello", "World"};

    var argument = argumentProcessingFactory.getArgument(tokens, tags, 3);

    assertNull(argument);
  }

  @Test
  public void getArgumentWhenCurrentTokenIndexIsEqualToTokenArrayLengthReturnsNull() {
    var tags = new Tag[]{ADJECTIVE, ADJECTIVE, ADJECTIVE, ADJECTIVE};
    var tokens = new String[]{"Hello", "World"};

    var argument = argumentProcessingFactory.getArgument(tokens, tags, 2);

    assertNull(argument);
  }

}
