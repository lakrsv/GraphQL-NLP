package com.temporalis.io.graphql.nlp.lang.processing;

import static com.temporalis.io.graphql.nlp.lang.processing.Tag.COORDINATING_CONJUNCTION;
import static com.temporalis.io.graphql.nlp.lang.processing.Tag.fromString;
import static com.temporalis.io.graphql.nlp.lang.processing.Tag.isAdjective;
import static com.temporalis.io.graphql.nlp.lang.processing.Tag.isNoun;
import static com.temporalis.io.graphql.nlp.lang.processing.Tag.isPronoun;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class TagTest {

  @Test
  public void isNounWithAllNounsIsTrue() {
    var allNouns =
        Arrays.stream(Tag.values())
            .filter(tag -> tag.name().toLowerCase().contains("noun") && !tag.name().toLowerCase().contains("pronoun"))
            .collect(toList());

    assertTrue(allNouns.stream().allMatch(Tag::isNoun));
  }

  @Test
  public void isNounWithAllNonNounsIsFalse() {
    var allNonNouns =
        Arrays.stream(Tag.values()).filter(tag -> !tag.name().toLowerCase().contains("noun"))
            .collect(toList());

    assertFalse(allNonNouns.stream().anyMatch(Tag::isNoun));
  }

  @Test
  public void isNounWithNullIsFalse() {
    assertFalse(isNoun(null));

  }

  @Test
  public void isPronounWithAllPronounsIsTrue() {
    var allPronouns =
        Arrays.stream(Tag.values()).filter(tag -> tag.name().toLowerCase().contains("pronoun"))
            .collect(toList());

    assertTrue(allPronouns.stream().allMatch(Tag::isPronoun));
  }

  @Test
  public void isPronounWithAllNonPronounsIsFalse() {
    var allNonpronouns =
        Arrays.stream(Tag.values()).filter(tag -> !tag.name().toLowerCase().contains("pronoun"))
            .collect(toList());

    assertFalse(allNonpronouns.stream().anyMatch(Tag::isPronoun));
  }

  @Test
  public void isPronounWithNullIsFalse() {
    assertFalse(isPronoun(null));
  }

  @Test
  public void isAdjectiveWithAllAdjectivesIsTrue() {
    var allAdjectives = Arrays.stream(Tag.values())
        .filter(tag -> tag.name().toLowerCase().contains("adjective")).collect(toList());

    assertTrue(allAdjectives.stream().allMatch(Tag::isAdjective));
  }

  @Test
  public void isAdjectiveWithAllNonAdjectivesIsFalse() {
    var allNonAdjectives = Arrays.stream(Tag.values())
        .filter(tag -> !tag.name().toLowerCase().contains("adjective")).collect(toList());

    assertFalse(allNonAdjectives.stream().anyMatch(Tag::isAdjective));
  }

  @Test
  public void isAdjectiveWithNullIsFalse() {
    assertFalse(isAdjective(null));
  }

  @Test
  public void fromStringWithNullReturnsNull() {
    assertNull(fromString(null));
  }

  @Test
  public void fromStringWithUnknownReturnsNull() {
    assertNull(fromString("HELLO WORLD"));
  }

  @Test
  public void fromStringWithRealTagWordReturnsExpectedTag() {
    var expectedTag = COORDINATING_CONJUNCTION;
    var expectedTagName = "CC";

    assertEquals(expectedTag, fromString(expectedTagName));
  }
}
