package com.github.lakrsv.graphql.nlp.schema.matchers;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.lakrsv.graphql.nlp.query.request.MatchOptions;
import com.github.lakrsv.graphql.nlp.schema.traversal.FieldInformation;
import com.github.lakrsv.graphql.nlp.schema.traversal.FieldInformationStub;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SeekingFieldMatcherTest {

  private static final String EDGE_NAME = "edges";
  private static final String NODE_NAME = "node";
  private static final String TARGET_NAME = "target";
  private SeekingFieldMatcher bruteForceSeekingFieldMatcher = new SeekingFieldMatcher(true);
  private SeekingFieldMatcher seekingFieldMatcher = new SeekingFieldMatcher(false);
  @Mock(lenient = true)
  private FieldInformation edgeField;
  @Mock(lenient = true)
  private FieldInformation nodeField;
  @Mock(lenient = true)
  private FieldInformation targetField;

  private MatchOptionFactory matchOptionFactory =
      new MatchOptionFactory(MatchOptions.builder().build(), emptyMap());

  private Map<String, FieldInformation> typeMap;

  @BeforeEach
  public void setup() {
    when(edgeField.getName()).thenReturn(EDGE_NAME);
    when(nodeField.getName()).thenReturn(NODE_NAME);
    when(targetField.getName()).thenReturn(TARGET_NAME);

    when(edgeField.hasChild(anyString())).thenReturn(true);
    when(edgeField.getChildren()).thenReturn(Map.of(NODE_NAME, nodeField));
    when(nodeField.getChildren()).thenReturn(Map.of(TARGET_NAME, targetField));
    when(edgeField.getChild(anyString())).thenReturn(nodeField);
    when(targetField.getChild(anyString())).thenReturn(targetField);
    when(targetField.getChildren()).thenReturn(Map.of(TARGET_NAME, new FieldInformationStub() {
    }));

    typeMap = Map.of(edgeField.getName(), edgeField);
  }

  @Test
  public void getClosestMatchingChildrenWithNullTargetReturnsEmptyList() {
    assertEquals(emptyList(),
        seekingFieldMatcher.getClosestMatchingChildren(null, typeMap, matchOptionFactory));
  }

  @Test
  public void getClosestMatchingChildrenWithEmptyTargetReturnsEmptyList() {
    assertEquals(emptyList(),
        seekingFieldMatcher.getClosestMatchingChildren("", typeMap, matchOptionFactory));
  }

  @Test
  public void getClosestMatchingChildrenUsesSpecificMatchOptions() {
    var matchOptionFactory =
        spy(new MatchOptionFactory(MatchOptions.builder().build(), emptyMap()));
    seekingFieldMatcher
        .getClosestMatchingChildren(TARGET_NAME, Map.of("target", new FieldInformationStub() {
        }), matchOptionFactory);

    verify(matchOptionFactory).getMatchOptions(eq(SeekingFieldMatcher.class));
  }

  @Test
  public void getClosestMatchingChildrenFindsMatchesOnMultipleLevelsOfDepthWithBruteforce() {
    var matches = bruteForceSeekingFieldMatcher
        .getClosestMatchingChildren(TARGET_NAME, typeMap, matchOptionFactory);
    assertEquals(2, matches.size());
  }

  @Test
  @Timeout(2)
  public void getClosestMatchingChildrenAvoidsCyclicReferencing() {
    when(targetField.getChildren()).thenReturn(Map.of("target", targetField));
    var matches = bruteForceSeekingFieldMatcher
        .getClosestMatchingChildren(TARGET_NAME, typeMap, matchOptionFactory);
    assertEquals(2, matches.size());
  }

  @Test
  public void getClosestMatchingChildrenFindsMatchesOnFirstLevelOfDepthWithoutBruteforce() {
    var matches = seekingFieldMatcher
        .getClosestMatchingChildren(TARGET_NAME, typeMap, matchOptionFactory);
    assertEquals(1, matches.size());
  }
}
