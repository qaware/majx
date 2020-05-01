![majx Logo](/doc/majx-logo-medium.png)
## Matching JSON expressively
[![Build Status](https://travis-ci.org/qaware/majx.svg?branch=master)](https://travis-ci.org/qaware/majx) [![Coverage Status](https://coveralls.io/repos/github/qaware/majx/badge.svg?branch=master)](https://coveralls.io/github/qaware/majx?branch=master) [![License](http://img.shields.io/badge/license-MIT-green.svg?style=flat)]() [![Download](https://api.bintray.com/packages/qaware-oss/maven/majx/images/download.svg) ](https://bintray.com/qaware-oss/maven/majx/_latestVersion)

Majx is a test library for the JVM written in Kotlin that verifies that a JSON document fulfils your expectations regarding
structure and values.

You provide two JSON documents

1. The **actual** JSON that should be tested
2. The **pattern** JSON that describes your expectations

We try to let you express almost all of your expectations in a flexible, readable
and concise way within the **pattern**. To achieve this, the **pattern** may contain magic
values, that are treated in a certain way.

# Usage

Obtain a reference to the **actual** and **pattern** JSONs as `String`  
or jackson's [`JsonNode`](https://fasterxml.github.io/jackson-databind/javadoc/2.8/com/fasterxml/jackson/databind/JsonNode.html)
and pass them to

- one of the static methods that the class `Majx` provides which throw `AssertionError`s
- or use the hamcrest matcher `matchesJson` from class `IsMatchingJson`

```
import static de.qaware.majx.Majx.assertJsonMatches;
import static de.qaware.majx.hamcrest.IsMatchingJson.matchesJson;

String actual  = "{ \"greeting\" : \"Hello, World!\", \"id\" : 12 }";
String pattern = "{ \"greeting\" : \"Hello, World!\", \"id\" : \"...\" }";

Majx.assertJsonMatches(pattern, actual);
assertThat(actual, matchesJson(pattern); // or use the hamcrest matcher
```

This test would succeed for any value for the `"id"` property inside the **actual** JSON
since its expected value is the magic `"..."`-wildcard value in the **pattern** 
(See [Ignoring values](https://github.com/qaware/majx/wiki/Ignoring-values)).

Details on all available features can be found in the wiki:

* [Matching properties and values exactly](https://github.com/qaware/majx/wiki/Matching-properties-and-values-exactly)
* [Partial object matching](https://github.com/qaware/majx/wiki/Partial-object-matching)
* [Partial array matching](https://github.com/qaware/majx/wiki/Partial-array-matching)
* [Ignoring values](https://github.com/qaware/majx/wiki/Ignoring-values)
* [Programmatic expectations](https://github.com/qaware/majx/wiki/Programmatic-expectations) through mustache expressions

## Dependencies

The JARs are available via Maven Central and JCenter. 

If you are using Maven to build your project, add the following to the `pom.xml` file.

```XML
<!-- https://mvnrepository.com/artifact/de.qaware.majx/majx -->
<dependency>
    <groupId>de.qaware.majx</groupId>
    <artifactId>majx</artifactId>
    <version>1.1.0</version>
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
    testCompile group: 'de.qaware.majx', name: 'majx', version: '1.1.0'
}
```

# Related Work

* Hamcrest matchers for JSON documents, https://github.com/hertzsprung/hamcrest-json
* Write JSON unit tests in less code, https://github.com/skyscreamer/JSONassert
* Compare JSON in your Unit Tests, https://github.com/lukas-krecan/JsonUnit

# Development Notes

See [Development Notes in the Wiki](https://github.com/qaware/majx/wiki/Development-Notes).

# Maintainer

Claudius Boettcher, <claudius.boettcher@qaware.de>.

# License

This software is provided under the MIT open source license, read the `LICENSE` file for details.
