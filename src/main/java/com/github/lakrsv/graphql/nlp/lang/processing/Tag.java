package com.github.lakrsv.graphql.nlp.lang.processing;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;

/**
 * Abstraction on-top of natural language processing tag terms in human-readable enumeration format.
 * <p>
 * Provides convenience methods to simplify logic
 */
@RequiredArgsConstructor
public enum Tag {COORDINATING_CONJUNCTION("CC"), CARDINAL_NUMBER("CD"), DETERMINER(
    "DT"), EXISTENTIAL("EX"), FOREIGN_WORD("FW"), PREPOSITION("IN"), ADJECTIVE(
    "JJ"), COMPARATIVE_ADJECTIVE("JJR"), SUPERLATIVE_ADJECTIVE("JJS"), LIST_ITEM_MARKER(
    "LS"), MODAL("MD"), SINGULAR_OR_MASS_NOUN("NN"), PLURAL_NOUN("NNS"), PROPER_SINGULAR_NOUN(
    "NNP"), PROPER_PLURAL_NOUN("NNPS"), PREDETERMINER("PDT"), POSSESSIVE_ENDING(
    "POS"), PERSONAL_PRONOUN("PRP"), POSSESSIVE_PRONOUN("PRP$"), ADVERB("RB"), COMPARATIVE_ADVERB(
    "RBR"), SUPERLATIVE_ADVERB("RBS"), PARTICLE("RP"), SYMBOL("SYM"), TO("to"), INTERJECTION(
    "UH"), BASE_VERB("VB"), PAST_TENSE_VERB("VBD"), GERUND_OR_PRESENT_PARTICIPLE_VERB(
    "VBG"), PAST_PARTICIPLE_VERB("VBN"), NON_3RD_PERSON_SINGULAR_PRESENT_VERB(
    "VBP"), THIRD_PERSON_SINGULAR_PRESENT_VERB("VBZ"), WH_DETEMINER("WDT"), WH_PRONOUN(
    "WP"), POSSESSIVE_WH_PRONOUN("WP$"), WH_ADVERB("WRB");

  private static final Map<String, Tag> TAGS_BY_WORD =
      Arrays.stream(values()).collect(toMap(t -> t.tag, t -> t));

  private static final Set<Tag> NOUNS = new HashSet<>() {{
    add(SINGULAR_OR_MASS_NOUN);
    add(PLURAL_NOUN);
    add(PROPER_SINGULAR_NOUN);
    add(PROPER_PLURAL_NOUN);
  }};

  private static final Set<Tag> PRONOUNS = new HashSet<>() {{
    add(PERSONAL_PRONOUN);
    add(POSSESSIVE_PRONOUN);
    add(WH_PRONOUN);
    add(POSSESSIVE_WH_PRONOUN);
  }};

  private static final Set<Tag> ADJECTIVES = new HashSet<>() {{
    add(ADJECTIVE);
    add(COMPARATIVE_ADJECTIVE);
    add(SUPERLATIVE_ADJECTIVE);
  }};

  private final String tag;

  /**
   * Check whether a {@link Tag} is a noun
   *
   * @param tag The {@link Tag} to check
   * @return Whether the {@link Tag} is a noun
   */
  public static boolean isNoun(Tag tag) {
    if (tag == null) {
      return false;
    }
    return NOUNS.contains(tag);
  }

  /**
   * Check whether a {@link Tag} is a pronoun
   *
   * @param tag The {@link Tag} to check
   * @return Whether the {@link Tag} is a pronoun
   */
  public static boolean isPronoun(Tag tag) {
    if (tag == null) {
      return false;
    }
    return PRONOUNS.contains(tag);
  }

  /**
   * Check whether a {@link Tag} is an adjective
   *
   * @param tag The {@link Tag} to check
   * @return Whether the {@link Tag} is an adjective
   */
  public static boolean isAdjective(Tag tag) {
    if (tag == null) {
      return false;
    }
    return ADJECTIVES.contains(tag);
  }

  /**
   * Creates a {@link Tag} from the NLP representation of it
   *
   * @param word The word to convert
   * @return A {@link Tag}, or null if it doesn't exist
   */
  public static Tag fromString(String word) {
    return TAGS_BY_WORD.getOrDefault(word, null);
  }}
