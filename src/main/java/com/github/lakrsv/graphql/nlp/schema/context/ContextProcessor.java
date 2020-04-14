package com.github.lakrsv.graphql.nlp.schema.context;

import com.github.lakrsv.graphql.nlp.schema.context.actions.ContextProcessorAction;
import java.util.List;
import lombok.NonNull;
import lombok.Value;

/**
 * A ContextProcessor holding actions to execute on context matching
 */
@Value
public class ContextProcessor {

  /**
   * The keyword this context processor matches against
   */
  @NonNull
  private final String keyword;
  /**
   * The actions to execute on context match
   */
  @NonNull
  private final List<ContextProcessorAction> actions;
}
