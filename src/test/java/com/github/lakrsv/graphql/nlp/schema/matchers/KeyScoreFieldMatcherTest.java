package com.github.lakrsv.graphql.nlp.schema.matchers;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.lakrsv.graphql.nlp.query.request.MatchOptions;
import com.github.lakrsv.graphql.nlp.schema.traversal.FieldInformation;
import com.github.lakrsv.graphql.nlp.schema.traversal.FieldInformationStub;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KeyScoreFieldMatcherTest {

  private static final KeyScoreFieldMatcher MATCHER = new KeyScoreFieldMatcher();

  private static final MatchOptionFactory MATCH_OPTION_FACTORY =
      new MatchOptionFactory(MatchOptions.builder().build(), emptyMap());


  @Test
  public void getClosestMatchingChildrenWithNullTargetReturnsEmptyList() {
    Assertions.assertEquals(emptyList(),
        MATCHER.getClosestMatchingChildren(null, Map.of("target", new FieldInformationStub() {
        }), MATCH_OPTION_FACTORY));

  }

  @Test
  public void getClosestMatchingChildrenWithEmptyTargetReturnsEmptyList() {
    assertEquals(emptyList(),
        MATCHER.getClosestMatchingChildren("", Map.of("target", new FieldInformationStub() {
        }), MATCH_OPTION_FACTORY));
  }

  @Test
  public void getClosestMatchingChildrenUsesMatchOptionsForClass() {
    var matchOptionFactory =
        spy(new MatchOptionFactory(MatchOptions.builder().build(), emptyMap()));
    MATCHER.getClosestMatchingChildren("target", Map.of("target", new FieldInformationStub() {
    }), matchOptionFactory);

    verify(matchOptionFactory).getMatchOptions(eq(KeyScoreFieldMatcher.class));
  }

  @Test
  public void getClosestMatchingChildrenDoesNotReturnChildrenWithLessThanMinimumSimilarity() {
    var matchOptionFactory =
        new MatchOptionFactory(MatchOptions.builder().minimumSimilarity(100).build(),
            emptyMap());
    var fieldInformation = mock(FieldInformation.class);
    when(fieldInformation.getName()).thenReturn("targe");

    var matches = MATCHER.getClosestMatchingChildren("target",
        Map.of(fieldInformation.getName(), fieldInformation), matchOptionFactory);

    assertTrue(matches.isEmpty());
  }

  @Test
  public void getClosestMatchingChildrenReturnsChildrenUpToLooseness() {
    var matchOptionFactory =
        new MatchOptionFactory(MatchOptions.builder().minimumSimilarity(0).looseness(4).build(),
            emptyMap());
    var fieldInformation = mock(FieldInformation.class);
    when(fieldInformation.getName()).thenReturn("targe");

    var typeMap = new HashMap<String, FieldInformation>();
    for (int i = 0; i < 10; i++) {
      typeMap.put(String.valueOf(i), fieldInformation);
    }

    var matches = MATCHER.getClosestMatchingChildren("target", typeMap, matchOptionFactory);

    Assertions.assertEquals(matchOptionFactory.getDefaultMatchOptions().getLooseness(), matches.size());
  }
}
