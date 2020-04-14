package com.github.lakrsv.graphql.nlp.query.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.lakrsv.graphql.nlp.exceptions.ArgumentException;
import com.github.lakrsv.graphql.nlp.lang.processing.Constants;
import com.github.lakrsv.graphql.nlp.schema.argument.InputArgument;
import com.github.lakrsv.graphql.nlp.schema.matchers.MatcherResult;
import com.github.lakrsv.graphql.nlp.schema.traversal.FieldInformation;
import com.github.lakrsv.graphql.nlp.schema.traversal.FieldInformationStub;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GraphQLSchemaResultTest {

  @Spy
  private FieldInformation fieldInformation = new FieldInformationStub() {
  };
  private MatcherResult<FieldInformation> matcherResult;

  @BeforeEach
  public void setup() {
    matcherResult = new MatcherResult<>("term", 100, fieldInformation);
  }

  @Test
  public void getResultReturnsExpectedResult() {
    var expectedMatchResult = new MatcherResult<>("expected", 100, fieldInformation);
    var result = new GraphQLSchemaResult(expectedMatchResult);
    assertEquals(expectedMatchResult, result.getResult());
  }

  @Test
  public void getAverageScoreCalculatesAverageScoreForAllChildren() {
    var score1 = 50;
    var score2 = 25;
    var score3 = 10;
    var score4 = matcherResult.getSimilarity();
    var expectedAverageScore = (score1 + score2 + score3 + score4) / 4f;

    var child1 = mock(SchemaResult.class);
    when(child1.getResult()).thenReturn(new MatcherResult<>("term", score1, fieldInformation));
    var child2 = mock(SchemaResult.class);
    when(child2.getResult()).thenReturn(new MatcherResult<>("term", score2, fieldInformation));
    var child3 = mock(SchemaResult.class);
    when(child3.getResult()).thenReturn(new MatcherResult<>("term", score3, fieldInformation));

    var result = new GraphQLSchemaResult(matcherResult);
    result.getChildren().addAll(List.of(child1, child2, child3));

    assertEquals(expectedAverageScore, result.getAverageScore());
  }

  @Test
  public void removeIncompleteTypesReturnsNullIfTypeIsIncomplete() {
    when(fieldInformation.getChildren())
        .thenReturn(Map.of("child1", new FieldInformationStub() {
        }));
    var incompleteResult = new GraphQLSchemaResult(matcherResult);

    assertNull(incompleteResult.removeIncompleteTypes());
  }

  @Test
  public void removeIncompleteTypesRemovesIncompleteChildrenAndReturnsSelf() {
    var completeChild =
        new GraphQLSchemaResult(new MatcherResult<>("term", 100, new FieldInformationStub() {
        }));
    var incompleteChild =
        new GraphQLSchemaResult(new MatcherResult<>("term", 100, new FieldInformationStub() {
          @Override
          public Map<String, FieldInformation> getChildren() {
            return Map.of("child1", new FieldInformationStub() {
            });
          }
        }));
    var root =
        new GraphQLSchemaResult(new MatcherResult<>("100", 100, new FieldInformationStub() {
        }));
    root.getChildren().addAll(List.of(completeChild, incompleteChild));

    var rootWithIncompleteRemoved = root.removeIncompleteTypes();
    assertEquals(rootWithIncompleteRemoved.getChildren(), List.of(completeChild));
  }

  @Test
  public void toQueryStringThrowsArgumentExceptionIfResultHasMissingRequiredArgumentValues() {
    var requiredIncompleteArgument = mock(InputArgument.class);
    when(requiredIncompleteArgument.isRequired()).thenReturn(true);
    when(requiredIncompleteArgument.getValue()).thenReturn(null);
    when(fieldInformation.getInputArguments()).thenReturn(List.of(requiredIncompleteArgument));
    var result = new GraphQLSchemaResult(new MatcherResult<>("ten", 100, fieldInformation));

    assertThrows(ArgumentException.class, result::toQueryString);
  }

  @Test
  public void toQueryStringHasEqualOpenAndClosedBracketCounts() {

    var result = createCompleteResult(100);
    var queryString = result.toQueryString();

    var openBracketCount =
        Arrays.stream(queryString.split("")).filter(c -> c.equals(Constants.OPEN_BRACKET))
            .count();
    var closedBracketCount =
        Arrays.stream(queryString.split("")).filter(c -> c.equals(Constants.CLOSED_BRACKET))
            .count();

    assertEquals(openBracketCount, closedBracketCount);
  }

  @Test
  public void toQueryStringHasEqualOpenAndClosedParanthesesCount() {
    var result = createCompleteResult(100);
    var queryString = result.toQueryString();

    var openBracketCount =
        Arrays.stream(queryString.split("")).filter(c -> c.equals(Constants.OPEN_PARANTHESES))
            .count();
    var closedBracketCount =
        Arrays.stream(queryString.split("")).filter(c -> c.equals(Constants.CLOSED_PARANTHESES))
            .count();

    assertEquals(openBracketCount, closedBracketCount);
  }

  @Test
  public void toQueryStringReturnsExpectedQueryString() {
    var expectedQueryString = "{fieldInformation(key: 100){child1 }}";

    var result = createCompleteResult(100);

    assertEquals(expectedQueryString, result.toQueryString());
  }

  @Test
  public void toQueryStringReturnsArgumentAsIntegerIfNotQuoted() {
    var result = createCompleteResult(100);

    assertTrue(result.toQueryString().contains("100"));
  }

  @Test
  public void toQueryStringReturnsArgumentAsIntegerIfQuoted() {
    var result = createCompleteResult("100");

    assertTrue(result.toQueryString().contains("100"));
  }

  @Test
  public void toQueryStringReturnsArgumentAsQuotedStringIfString() {
    var result = createCompleteResult("hello");

    assertTrue(result.toQueryString().contains("\"hello\""));
  }

  @Test
  public void toQueryStringContainsFieldInformationName() {
    var result = createCompleteResult("hello");

    assertTrue(result.toQueryString().contains(fieldInformation.getName()));
  }

  private GraphQLSchemaResult createCompleteResult(Object argumentValue) {
    var requiredCompleteArgument = mock(InputArgument.class);
    when(requiredCompleteArgument.getKey()).thenReturn("key");
    when(requiredCompleteArgument.getValue()).thenReturn(argumentValue);

    when(fieldInformation.getInputArguments()).thenReturn(List.of(requiredCompleteArgument));
    when(fieldInformation.getName()).thenReturn("fieldInformation");

    var child1 = new FieldInformationStub() {
      @Override
      public String getName() {
        return "child1";
      }
    };

    var result = new GraphQLSchemaResult(new MatcherResult<>("ten", 100, fieldInformation));
    result.getChildren()
        .add(new GraphQLSchemaResult(new MatcherResult<>("child1", 100, child1)));

    return result;
  }
}
