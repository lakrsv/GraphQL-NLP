package com.temporalis.io.graphql.nlp.schema.matchers;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.temporalis.io.graphql.nlp.query.request.MatchOptions;
import com.temporalis.io.graphql.nlp.schema.traversal.FieldInformation;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConnectionPatternFieldMatcherTest {

  private static final String EDGE_NAME = "edges";
  private static final String NODE_NAME = "node";
  private static final String TARGET_NAME = "target";
  @Mock(lenient = true)
  private FieldInformation edgeField;
  @Mock(lenient = true)
  private FieldInformation nodeField;
  @Mock(lenient = true)
  private FieldInformation targetField;
  @Spy
  private FieldMatcher innerMatcher = new KeyScoreFieldMatcher();

  private MatchOptionFactory matchOptionFactory =
      new MatchOptionFactory(MatchOptions.builder().build(), emptyMap());

  private Map<String, FieldInformation> typeMap;

  private ConnectionPatternFieldMatcher matcher;

  @BeforeEach
  public void setup() {
    when(edgeField.getName()).thenReturn(EDGE_NAME);
    when(nodeField.getName()).thenReturn(NODE_NAME);
    when(targetField.getName()).thenReturn(TARGET_NAME);

    when(edgeField.hasChild(anyString())).thenReturn(true);
    when(edgeField.getChildren()).thenReturn(Map.of(NODE_NAME, nodeField));
    when(nodeField.getChildren()).thenReturn(Map.of(TARGET_NAME, targetField));
    when(edgeField.getChild(anyString())).thenReturn(nodeField);

    typeMap = Map.of(edgeField.getName(), edgeField);
    matcher = new ConnectionPatternFieldMatcher(innerMatcher, EDGE_NAME, NODE_NAME);
  }

  @Test
  public void getClosestMatchingChildrenWhenEdgeDoesNotExistReturnsEmptyList() {
    var matches =
        matcher.getClosestMatchingChildren(TARGET_NAME, emptyMap(), matchOptionFactory);
    assertEquals(emptyList(), matches);
  }

  @Test
  public void getClosestMatchingChildrenWhenNodeDoesNotExistReturnsEmptyList() {
    var matches = matcher
        .getClosestMatchingChildren(TARGET_NAME, Map.of(EDGE_NAME, targetField),
            matchOptionFactory);
    assertEquals(emptyList(), matches);
  }

  @Test
  public void getClosestMatchingChildrenUsesInternalMatcher() {
    matcher.getClosestMatchingChildren(TARGET_NAME, typeMap, matchOptionFactory);
    verify(innerMatcher).getClosestMatchingChildren(anyString(), anyMap(), any());
  }

  @Test
  public void getClosestMatchingChildrenReturnsExpectedTarget() {
    var matches = matcher.getClosestMatchingChildren(TARGET_NAME, typeMap, matchOptionFactory);
    Assertions.assertEquals(targetField, matches.get(0).getResult());
  }

  @Test
  public void getClosestMatchingChildrenTargetHasExpectedInnerPath() {
    var matches = matcher.getClosestMatchingChildren(TARGET_NAME, typeMap, matchOptionFactory);
    var innerPath = matches.get(0).getInnerPath();

    Assertions.assertEquals(EDGE_NAME, innerPath.get(0).getName());
    Assertions.assertEquals(NODE_NAME, innerPath.get(1).getName());
  }
}
