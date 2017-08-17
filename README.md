![majx Logo](/doc/majx-logo-medium.png)
## Matching JSON expressively
[![Build Status](https://travis-ci.org/qaware/majx.svg?branch=master)](https://travis-ci.org/qaware/majx) [![Coverage Status](https://coveralls.io/repos/github/qaware/majx/badge.svg?branch=master)](https://coveralls.io/github/qaware/majx?branch=master) [![License](http://img.shields.io/badge/license-MIT-green.svg?style=flat)]() [![Download](https://api.bintray.com/packages/qaware-oss/maven/majx/images/download.svg) ](https://bintray.com/qaware-oss/maven/majx/_latestVersion)

Majx is a test library that helps to verify that a JSON document fulfils your expectations regarding
structure and values.

You provide two JSON documents

1. The **pattern** JSON that describes your expectations
2. The **actual** JSON that should be tested

We try to let you express almost all of your expectations in a flexible, readable
and concise way within the **pattern**. To achieve this, the **pattern** may contain magic
values, that are treated in a certain way.

# Usage

## Dependencies

The JARs are available via Maven Central and JCenter. 

If you are using Maven to build your project, add the following to the `pom.xml` file.

```XML
<!-- https://mvnrepository.com/artifact/de.qaware.majx/majx -->
<dependency>
    <groupId>de.qaware.majx</groupId>
    <artifactId>majx</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

In case you are using Gradle to build your project, add the following to the `build.gradle` file:

```groovy
repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/de.qaware.majx/majx
    testCompile group: 'de.qaware.majx', name: 'majx', version: '1.0.0'
}
```

## Full example

The following example shows all majx features. For details on individual features
refer to the [wiki](https://github.com/qaware/majx/wiki).

Full example **pattern**:

```
{
  "brand" : "BMW",              (1)
  "color" : "...",              (2)
  "engine" : {                  (3)
    "cylinders" : 4,            (4)
    "..." : "..."               (5)
  },
  "features" : [                (6)
    "air con",                  (7)
    "..."                       (8)
  ],
  "url" : "{{baseUrl}}/cars/12" (9)
}                               (10)
```

An **actual** JSON that you match against this pattern ...

1. must have a property ``$.brand`` of type string with value ``"BMW"``.
2. must have a property ``$.color`` of any type with any value.
3. must have a property ``$.engine`` of type object.
4. must have a property ``$.engine.cylinders`` of type number with value ``4``
5. the object at position ``$.engine`` may have zero or more other properties, which we don't care about. The wildcard-property (``"..." : "..."``) must be the last entry of an object.
6. must have a property ``$.features`` of type array.
7. must have a property ``$.features[0]`` of type string with value ``"air con"``.
8. the array at position ``$.features`` may have zero or more other properties, which we don't care about. The wildcard element (``"..."``) must be the last entry of any array.
9. must have a property ``$.url`` of type string. The expected value is
determined at runtime by looking up the mustache expression ``{{baseUrl}}`` in a given mustache scope. The
 mustache scope can be a ``Map<String,String>`` or a POJO. If no mustache expressions are used in the pattern,
 no mustache scope must be provided.
10. Note: The root object (``$``) may not have additional properties (as opposed to ``$.engine``, see above)
because it has no wildcard-property (``"..." : "..."``) at the end.

## Matching an actual JSON document

That means this **actual** JSON will pass the validation given
that you provide a mapping ``baseUrl=https://base.com`` in the mustache scope:

```
{
  "brand" : "BMW",
  "color" : "red",
  "engine" : {
    "cylinders" : 4
  },
  "features" : [
    "air con",
    "head up display",
    "brake assist"
  ],
  "url" : "https://base.com/cars/12"
}
```

... but this **actual** JSON will cause a validation error given
 that you provide a mapping ``baseUrl=https://base.com`` in the mustache scope:

```
{
  "brand" : "VW",                       (1)
  "engine" : {
    "cylinders" : "4"                   (2)
  },
  "features" : [
    "head up display",
    "air con"                           (3)
  ],
  "url" : "https://other.com/cars/12"   (4)
}                                       (5)
```

Why? Because the latter **actual** JSON ...

1. does not have a property ``$.brand`` of type string with value``"BMW"``. Instead it's value is ``"VW"``.
2. does not have a property ``$.engine.cylinders`` of type number with value ``4``. Instead it is of type
string and has value ``"4"``.
3. does not have a property ``$.features[0]`` of type string with value ``"air con"``.
Instead, it's value is ``"head up display"``.
4. does not have a property ``$.url`` with value ``"https://base.com/cars/12"``. Instead it's value
is ``"https://other.com/cars/12"``.
5. does not have a property ``$.color`` with any value.

## Usage

```
// Obtain actual and pattern JSONs as strings or jackson JsonNodes.
String actual = ...;
String pattern = readResource("path/to/my/pattern.json");

// Create a mustache scope (if pattern contains mustache expressions).
Map<String,String> mustacheScope = ImmutableMap.<String, String>builder()
    .put("baseUrl", "https://base.com")
    .build()

// Validate
Majx.assertJsonMatches(pattern, actual, mustacheScope);
```

If the actual JSON does not match the pattern the error message might look like this:

```
Error at location $.url: Value does not match. Pattern was evaluated as mustache expression.
Original pattern: {{baseUrl}}/path/to/file
Expected: is "https://base.com/path/to/file"
     but: was "https://other.com/path/to/file".

--------------------------------------------------------------------------------------------
Actual JSON
--------------------------------------------------------------------------------------------
{
  "url" : "https://other.com/path/to/file"
}

--------------------------------------------------------------------------------------------
Pattern
--------------------------------------------------------------------------------------------
{
  "url" : "{{baseUrl}}/path/to/file"
}

--------------------------------------------------------------------------------------------
Mustache Scope
--------------------------------------------------------------------------------------------
baseUrl = https://base.com
```

## Related Work

* Hamcrest matchers for JSON documents, https://github.com/hertzsprung/hamcrest-json
* Write JSON unit tests in less code, https://github.com/skyscreamer/JSONassert
* Compare JSON in your Unit Tests, https://github.com/lukas-krecan/JsonUnit

## Maintainer

Claudius Boettcher, <claudius.boettcher@qaware.de>.

## License

This software is provided under the MIT open source license, read the `LICENSE` file for details.
