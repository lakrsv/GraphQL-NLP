# GraphQL NLP
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3a443fb183114c62af4ae900487c781f)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lakrsv/GraphQL-NLP&amp;utm_campaign=Badge_Grade)

This library is responsible for converting input language to syntactically correct
GraphQL queries, based on supplied configuration.
## Motivation
GraphQL is rapidly being adopted as an alternative to REST.
GraphQL schemas aggregate data, allowing consumers to compose queries to fit their needs. 
However, the amount of possible permutations in a schema makes it difficult to retrieve information from GraphQL endpoints. 


This project aims to simplify data retrieval from these GraphQL
endpoints, using natural language processing to infer what a
consumer is asking for.

## Quickstart
The `QueryGenerator` interface  is the main entrypoint for this library, implemented by `QueryGeneratorImpl`. 
The `QueryGeneratorImpl` class exposes a builder which accepts configuration to tailor the functionality
of the library, after which the `convertToQuery(QueryRequest queryRequest)` method can be called.

The builder may be instantiated with

```
var queryGeneratorBuilder = QueryGeneratorImpl.builder();
```

### Configuring the QueryGenerator
The `QueryGeneratorImpl` may be configured prior to usage. Certain configuration is required,
while some configuration is optional.

#### Configuring the schema
The `QueryGeneratorImpl` must be configured with a `GraphQLSchema` prior to invoking query generation.

```
SchemaParser schemaParser = new SchemaParser();
var schema = SchemaConfiguration.class.getResourceAsStream(schemaProperties.getLocation());
var typeDefinitionRegistry = schemaParser.parse(new InputStreamReader(schema));
var schema = UnExecutableSchemaGenerator.makeUnExecutableSchema(typeDefinitionRegistry);

queryGeneratorBuilder.schema(schema);
```

#### Configuring synonyms
The builder may optionally be configured with synonyms to substitute for a certain keyword
```
var synonyms = Map.of(
    "organizations", Set.of("orgs"),
    "organization", Set.of("org"),
    "assets", Set.of("employees", "users"),
    "asset", Set.of("employee", "user")
);

queryGeneratorBuilder.synonyms(synonyms);
```

#### Configuring default type arguments
The builder may also be configured with default type arguments to use if arguments for certain
types were not detected

```
var defaultTypeArguments = new ArrayList<DefaultTypeArguments>() {{
  add(new DefaultTypeArguments("assets", new ArrayList<>() {{
    add(new Argument("first", 10000));
  }}));
  add(new DefaultTypeArguments("organization", new ArrayList<>() {{
    add(new Argument("id", "Google"));
  }}));
}};

queryGeneratorBuilder.defaultTypeArguments(defaultTypeArguments);
```

#### Configuring matchers
The builder accepts a chained field matcher with custom matchers. These matchers may be injected
into the query generator to tailor field matching functionality.

```
var matchers = ChainedFieldMatcher.builder().customMatchers(new FieldMatcher[]{
    new ConnectionPatternFieldMatcher(new KeyScoreFieldMatcher(), "edges", "node"),
    new KeyScoreFieldMatcher(),
    new SeekingFieldMatcher(true)}).build();
    
queryGeneratorBuilder.chainedFieldMatcher(matchers);
```

### Invoking the QueryGenerator
Finally, once the `QueryGenerator` has been configured, it may be instantiated by calling
```
var queryGenerator = queryGeneratorBuilder.build();
```

The `QueryGenerator` is now ready to convert language to GraphQL queries. 
The `QueryRequest` represents the payload describing the text to convert, and the options
used to modify how the query is executed.

It may be instantiated with a builder
```
var request = QueryRequest.builder()
    .text("Show me the first 100 tweets I made in January")
    // These are optional
    .options(...)
    .build();
```

The request may be used to invoke the `QueryGenerator`, which produces a `QueryResult`.
```
var result = queryGenerator.convertToQuery(request);
```

#### The QueryResult

The `QueryResult` exposes two methods, `getBestMatch()` and `getAllMatches()`, for retrieving results.
```
var schemaResult = result.getBestMatch().get();
```

Once a `SchemaResult` has been retrieved, one should call `getMissingRequiredArgumentsByField()` to check
if there are missing required arguments. These may be populated post query construction.

Once this is done, invoke `toQueryString()` to create a syntactically valid GraphQL query which
may be invoked against the GraphQL endpoint implementing the supplied schema.

```
if(schemaResult.getMissingRequiredArgumentsByField().isEmpty() {
    var query = schemaResult.toQueryString();
}
```


## Code style
This project uses Google Styles. When developing for this project, please use the supplied GoogleStyle.xml
located at the root of the project directory.
Using IntelliJ, this is how you can enable google styles for the project:
1. Open Preferences
2. Select Editor/Code Style
3. Click the settings cog next to Scheme
4. Select Import Scheme/IntelliJ IDEA code style xml
5. Select the GoogleStyle.xml and import it
For any other editor, please follow the instructions specific to that editor for importing
code style.
## Inspections
This project uses static code analysis to ensure that committed code follows the same
conventions, and to prevent common mistakes and to give developers hints on how they
can make their code more readable. 
The inspections exist in the root of the project directory,
and the file is named Inspections.xml
Using IntelliJ, this is how you can enable the inspections for the project:
1. Open Preferences
2. Select Editor/Inspections
3. Click the settings cog next to Profile
4. Select Import Profile
5. Select the Inspections.xml and import it
