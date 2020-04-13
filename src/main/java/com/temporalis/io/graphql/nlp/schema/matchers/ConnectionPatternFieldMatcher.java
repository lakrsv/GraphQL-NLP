package com.temporalis.io.graphql.nlp.schema.matchers;

import static java.util.Collections.emptyList;

import com.temporalis.io.graphql.nlp.schema.traversal.FieldInformation;
import com.temporalis.io.graphql.nlp.schema.traversal.FieldInformationStub;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

/**
 * {@inheritDoc}
 * <p>
 * Custom matcher for the common pagination pattern used in GraphQL APIs
 */
@RequiredArgsConstructor
public class ConnectionPatternFieldMatcher implements FieldMatcher {

  private final FieldMatcher innerMatcher;
  /**
   * The name of the edge type
   */
  private final String edgeName;
  /**
   * The name of the node type
   */
  private final String nodeName;

  /**
   * {@inheritDoc}
   */
  @Override
  public List<MatcherResult<FieldInformation>> getClosestMatchingChildren(String target,
      Map<String, FieldInformation> typeMap, MatchOptionFactory matchOptionFactory) {
    if (!typeMap.containsKey(edgeName)) {
      return emptyList();
    }
    var edgeMap = typeMap.get(edgeName);
    if (!edgeMap.hasChild(nodeName)) {
      return emptyList();
    }

    var nodeMap = edgeMap.getChild(nodeName);
    var matches = innerMatcher
        .getClosestMatchingChildren(target, nodeMap.getChildren(), matchOptionFactory);
    matches.forEach(match -> {
      match.getInnerPath().add(new FieldInformationStub() {
        @Override
        public String getName() {
          return edgeName;
        }
      });
      match.getInnerPath().add(new FieldInformationStub() {
        @Override
        public String getName() {
          return nodeName;
        }
      });
    });
    return matches;
  }
}
