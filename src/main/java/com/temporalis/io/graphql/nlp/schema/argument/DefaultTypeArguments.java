package com.temporalis.io.graphql.nlp.schema.argument;

import static java.util.stream.Collectors.toMap;

import com.temporalis.io.graphql.nlp.exceptions.ArgumentException;
import java.util.List;
import java.util.Map;
import lombok.Value;

/**
 * Default arguments for a specific type.
 */
@Value
public class DefaultTypeArguments {

  private final String typeName;
  private final Map<String, Object> argumentsByKey;

  public DefaultTypeArguments(String typeName, List<Argument> arguments) {
    this.typeName = typeName;
    argumentsByKey = arguments.stream().peek(this::validateArgument)
        .collect(toMap(Argument::getKey, Argument::getValue));
  }

  private void validateArgument(Argument argument) {
    if (argument.getKey() == null || argument.getKey().isEmpty()) {
      throw new ArgumentException("Default argument key can not be null or empty");
    }
    if (argument.getValue() == null) {
      throw new ArgumentException("Default argument value can not be null");
    }
  }
}
