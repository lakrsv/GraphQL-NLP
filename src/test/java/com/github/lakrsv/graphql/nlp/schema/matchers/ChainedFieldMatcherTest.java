package com.github.lakrsv.graphql.nlp.schema.matchers;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.lakrsv.graphql.nlp.query.request.MatchOptions;
import com.github.lakrsv.graphql.nlp.schema.traversal.FieldInformation;
import com.github.lakrsv.graphql.nlp.schema.traversal.FieldInformationStub;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class ChainedFieldMatcherTest {

  private static final ChainedFieldMatcher CHAINED_FIELD_MATCHER =
      ChainedFieldMatcher.builder().build();

  @Test
  public void constructorWithNullDefaultMatcherThrowsNullPointerException() {
    assertThrows(NullPointerException.class,
        () -> ChainedFieldMatcher.builder().defaultMatcher(null).build());
  }

  @Test
  public void constructorWithnullCustomMatchersThrowsNullPointerException() {
    assertThrows(NullPointerException.class,
        () -> ChainedFieldMatcher.builder().customMatchers(null).build());
  }

  @Test
  public void getClosestMatchingChildrenReturnsExpectedKeyWithNoCustomMatcher() {
    var expectedTarget = "target";
    var expectedTargetResult = new FieldInformationStub() {
      @Override
      public String getName() {
        return expectedTarget;
      }
    };
    var decoyResult = new FieldInformationStub() {
      @Override
      public String getName() {
        return "targe";
      }
    };
    Map<String, FieldInformation> fields =
        Map.of(expectedTargetResult.getName(), expectedTargetResult, decoyResult.getName(),
            decoyResult);
    var closestMatchingChildren = CHAINED_FIELD_MATCHER
        .getClosestMatchingChildren(expectedTarget, fields,
            new MatchOptionFactory(MatchOptions.builder().build(), Collections.emptyMap()));

    assertEquals(expectedTargetResult, closestMatchingChildren.get(0).getResult());
  }

  @Test
  public void getClosestMatchingChildrenUsesCustomMatcherIfSpecified() {
    var expectedTarget = "target";
    var expectedTargetResult = new FieldInformationStub() {
      @Override
      public String getName() {
        return expectedTarget;
      }
    };
    var decoyResult = new FieldInformationStub() {
      @Override
      public String getName() {
        return "targe";
      }
    };

    var customMatcher = new FieldMatcher() {
      @Override
      public List<MatcherResult<FieldInformation>> getClosestMatchingChildren(String target,
          Map<String, FieldInformation> typeMap, MatchOptionFactory matchOptionFactory) {
        return List.of(new MatcherResult<>("dldldl", 100, decoyResult));
      }
    };

    var chainedFieldMatcher =
        ChainedFieldMatcher.builder().customMatchers(new FieldMatcher[]{(customMatcher)})
            .build();

    Map<String, FieldInformation> fields =
        Map.of(expectedTargetResult.getName(), expectedTargetResult, decoyResult.getName(),
            decoyResult);
    var closestMatchingChildren = chainedFieldMatcher
        .getClosestMatchingChildren(expectedTarget, fields,
            new MatchOptionFactory(MatchOptions.builder().build(), Collections.emptyMap()));

    assertArrayEquals(new FieldInformation[]{decoyResult},
        closestMatchingChildren.stream().map(MatcherResult::getResult).toArray());
  }

  @Test
  public void getClosestMatchingChildrenUsesCustomMatchersInDescendingOrder() {
    var firstMatcher = mock(FieldMatcher.class);
    when(firstMatcher.getClosestMatchingChildren(anyString(), anyMap(), any()))
        .thenReturn(emptyList());
    var expectedTarget = "target";
    var expectedTargetResult = new FieldInformationStub() {
      @Override
      public String getName() {
        return expectedTarget;
      }
    };
    var decoyResult = new FieldInformationStub() {
      @Override
      public String getName() {
        return "targe";
      }
    };

    var customMatcher = new FieldMatcher() {
      @Override
      public List<MatcherResult<FieldInformation>> getClosestMatchingChildren(String target,
          Map<String, FieldInformation> typeMap, MatchOptionFactory matchOptionFactory) {
        return List.of(new MatcherResult<>("dldldl", 100, decoyResult));
      }
    };

    var chainedFieldMatcher = ChainedFieldMatcher.builder()
        .customMatchers(new FieldMatcher[]{firstMatcher, customMatcher}).build();

    Map<String, FieldInformation> fields =
        Map.of(expectedTargetResult.getName(), expectedTargetResult, decoyResult.getName(),
            decoyResult);
    var closestMatchingChildren = chainedFieldMatcher
        .getClosestMatchingChildren(expectedTarget, fields,
            new MatchOptionFactory(MatchOptions.builder().build(), Collections.emptyMap()));

    assertArrayEquals(new FieldInformation[]{decoyResult},
        closestMatchingChildren.stream().map(MatcherResult::getResult).toArray());

    verify(firstMatcher).getClosestMatchingChildren(anyString(), anyMap(), any());
  }
}
