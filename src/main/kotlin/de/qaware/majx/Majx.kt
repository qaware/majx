@file:JvmName("Majx")
/**
 * MIT License
 *
 * Copyright (c) 2017 QAware GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.qaware.majx

import com.fasterxml.jackson.databind.JsonNode
import java.io.IOException

/**
 * Asserts that the given actual JSON matches the given JSON pattern.
 *
 * If the JSON does not match, an [AssertionError] is thrown.
 *
 * @param pattern The pattern JSON string.
 * @param actual The actual JSON string to match against the pattern.
 */
fun assertJsonMatches(pattern: String, actual: String) =
        assertJsonMatches(null, pattern, actual)

/**
 * Asserts that the given actual JSON matches the given JSON pattern ignoring the order of array elements.
 *
 * If the JSON does not match, an [AssertionError] is thrown.
 *
 * @param pattern The pattern JSON string.
 * @param actual The actual JSON string to match against the pattern.
 */
fun assertJsonMatchesAnyArrayOrder(pattern: String, actual: String) =
        assertJsonMatchesAnyArrayOrder(null, pattern, actual)

/**
 * Asserts that the given actual JSON [JsonNode] matches the given JSON pattern [JsonNode].
 *
 * If the JSON does not match, an [AssertionError] is thrown.
 *
 * @param pattern The pattern JSON node.
 * @param actual The actual JSON node to match against the pattern.
 */
fun assertJsonMatches(pattern: JsonNode, actual: JsonNode) =
        assertJsonMatches(null, pattern, actual, null)

/**
 * Asserts that the given actual JSON [JsonNode] matches the given JSON pattern [JsonNode] ignoring the order of array elements.
 *
 * If the JSON does not match, an [AssertionError] is thrown.
 *
 * @param pattern The pattern JSON node.
 * @param actual The actual JSON node to match against the pattern.
 */
fun assertJsonMatchesAnyArrayOrder(pattern: JsonNode, actual: JsonNode) =
        assertJsonMatchesAnyArrayOrder(null, pattern, actual, null)

/**
 * Asserts that the given actual JSON [JsonNode] matches the given JSON pattern string.
 *
 * If the JSON does not match, an [AssertionError] is thrown.
 *
 * @param pattern The pattern JSON string.
 * @param actual The actual JSON node to match against the pattern.
 */
fun assertJsonMatches(pattern: String, actual: JsonNode) =
        assertJsonMatches(null, parseAndValidate(pattern, "pattern"), actual, null)

/**
 * Asserts that the given actual JSON [JsonNode] matches the given JSON pattern string ignoring the order of array elements.
 *
 * If the JSON does not match, an [AssertionError] is thrown.
 *
 * @param pattern The pattern JSON string.
 * @param actual The actual JSON node to match against the pattern.
 */
fun assertJsonMatchesAnyArrayOrder(pattern: String, actual: JsonNode) =
        assertJsonMatchesAnyArrayOrder(null, parseAndValidate(pattern, "pattern"), actual, null)


/**
 * Asserts that the given actual JSON string matches the given JSON pattern [JsonNode].
 *
 * If the JSON does not match, an [AssertionError] is thrown.
 *
 * @param pattern The pattern JSON string.
 * @param actual The actual JSON node to match against the pattern.
 */
fun assertJsonMatches(pattern: JsonNode, actual: String) =
        assertJsonMatches(null, pattern, parseAndValidate(actual, "actual"), null)

/**
 * Asserts that the given actual JSON string matches the given JSON pattern [JsonNode] ignoring the order of array elements.
 *
 * If the JSON does not match, an [AssertionError] is thrown.
 *
 * @param pattern The pattern JSON string.
 * @param actual The actual JSON node to match against the pattern.
 */
fun assertJsonMatchesAnyArrayOrder(pattern: JsonNode, actual: String) =
        assertJsonMatchesAnyArrayOrder(null, pattern, parseAndValidate(actual, "actual"), null)


/**
 * Asserts that the given actual JSON matches the given JSON pattern by evaluating mustache expressions
 * with the given mustache scope.
 *
 * The mustache scope may be a <code>Map<String,String></code> or a POJO.
 *
 * If the JSON does not match, an [AssertionError] is thrown.
 *
 * @param pattern The pattern JSON string.
 * @param actual The actual JSON string to match against the pattern.
 * @param mustacheScope A Map<String,String> or a POJO containing mustache expressions.
 * @see <a href="https://github.com/qaware/majx">README</a> for details.
 */
fun assertJsonMatches(pattern: String, actual: String, mustacheScope: Any? = null) =
        assertJsonMatches(null, pattern, actual, mustacheScope)

/**
 * Asserts that the given actual JSON matches the given JSON pattern by evaluating mustache expressions
 * with the given mustache scope and ignoring the order of array elements.
 *
 * The mustache scope may be a <code>Map<String,String></code> or a POJO.
 *
 * If the JSON does not match, an [AssertionError] is thrown.
 *
 * @param pattern The pattern JSON string.
 * @param actual The actual JSON string to match against the pattern.
 * @param mustacheScope A Map<String,String> or a POJO containing mustache expressions.
 * @see <a href="https://github.com/qaware/majx">README</a> for details.
 */
fun assertJsonMatchesAnyArrayOrder(pattern: String, actual: String, mustacheScope: Any? = null) =
        assertJsonMatchesAnyArrayOrder(null, pattern, actual, mustacheScope)


/**
 * Asserts that the given actual JSON [JsonNode] matches the given JSON pattern [JsonNode]
 * by evaluating mustache expressions with the given mustache scope.
 *
 * The mustache scope may be a <code>Map<String,String></code> or a POJO.
 *
 * If the JSON does not match, an [AssertionError] is thrown.
 *
 * @param pattern The pattern JSON node.
 * @param actual The actual JSON node to match against the pattern.
 * @param mustacheScope A Map<String,String> or a POJO containing mustache expressions.
 * @see <a href="https://github.com/qaware/majx">README</a> for details.
 */
fun assertJsonMatches(pattern: JsonNode, actual: JsonNode, mustacheScope: Any? = null) =
        assertJsonMatches(null, pattern, actual, mustacheScope)

/**
 * Asserts that the given actual JSON [JsonNode] matches the given JSON pattern [JsonNode]
 * by evaluating mustache expressions with the given mustache scope and ignoring the order of array elements.
 *
 * The mustache scope may be a <code>Map<String,String></code> or a POJO.
 *
 * If the JSON does not match, an [AssertionError] is thrown.
 *
 * @param pattern The pattern JSON node.
 * @param actual The actual JSON node to match against the pattern.
 * @param mustacheScope A Map<String,String> or a POJO containing mustache expressions.
 * @see <a href="https://github.com/qaware/majx">README</a> for details.
 */
fun assertJsonMatchesAnyArrayOrder(pattern: JsonNode, actual: JsonNode, mustacheScope: Any? = null) =
        assertJsonMatchesAnyArrayOrder(null, pattern, actual, mustacheScope)


/**
 * Asserts that the given actual JSON matches the given JSON pattern by evaluating mustache expressions
 * with the given mustache scope.
 *
 * The mustache scope may be a <code>Map<String,String></code> or a POJO.
 *
 * If the JSON does not match, an [AssertionError] is thrown.
 *
 * @param reason A custom message to prepend to the generated majx error.
 * @param pattern The pattern JSON string.
 * @param actual The actual JSON string to match against the pattern.
 * @param mustacheScope A Map<String,String> or a POJO containing mustache expressions.
 * @see <a href="https://github.com/qaware/majx">README</a> for details.
 */
fun assertJsonMatches(reason: String?, pattern: String, actual: String, mustacheScope: Any? = null) {
    assertJsonMatches(reason,
            parseAndValidate(pattern, "pattern"),
            parseAndValidate(actual, "actual"),
            mustacheScope)
}

/**
 * Asserts that the given actual JSON matches the given JSON pattern by evaluating mustache expressions
 * with the given mustache scope and ignoring the order of array elements.
 *
 * The mustache scope may be a <code>Map<String,String></code> or a POJO.
 *
 * If the JSON does not match, an [AssertionError] is thrown.
 *
 * @param reason A custom message to prepend to the generated majx error.
 * @param pattern The pattern JSON string.
 * @param actual The actual JSON string to match against the pattern.
 * @param mustacheScope A Map<String,String> or a POJO containing mustache expressions.
 * @see <a href="https://github.com/qaware/majx">README</a> for details.
 */
fun assertJsonMatchesAnyArrayOrder(reason: String?, pattern: String, actual: String, mustacheScope: Any? = null) {
    assertJsonMatchesAnyArrayOrder(reason,
            parseAndValidate(pattern, "pattern"),
            parseAndValidate(actual, "actual"),
            mustacheScope)
}

/**
 * Asserts that the given actual JSON [JsonNode] matches the given JSON pattern [JsonNode] by evaluating
 * mustache expressions with the given mustache scope.
 *
 * The mustache scope may be a <code>Map<String,String></code> or a POJO.
 *
 * If the JSON does not match, an [AssertionError] is thrown.
 *
 * @param reason A custom message to prepend to the generated majx error.
 * @param pattern The pattern JSON node.
 * @param actual The actual JSON node to match against the pattern.
 * @param mustacheScope A Map<String,String> or a POJO containing mustache expressions.
 * @see <a href="https://github.com/qaware/majx">README</a> for details.
 */
fun assertJsonMatches(reason: String?, pattern: JsonNode, actual: JsonNode, mustacheScope: Any?) =
        assertJsonMatchesInternal(mustacheScope, reason, pattern, actual)

/**
 * Asserts that the given actual JSON [JsonNode] matches the given JSON pattern [JsonNode] by evaluating
 * mustache expressions with the given mustache scope and ignoring the order of array elements.
 *
 * The mustache scope may be a <code>Map<String,String></code> or a POJO.
 *
 * If the JSON does not match, an [AssertionError] is thrown.
 *
 * @param reason A custom message to prepend to the generated majx error.
 * @param pattern The pattern JSON node.
 * @param actual The actual JSON node to match against the pattern.
 * @param mustacheScope A Map<String,String> or a POJO containing mustache expressions.
 * @see <a href="https://github.com/qaware/majx">README</a> for details.
 */
fun assertJsonMatchesAnyArrayOrder(reason: String?, pattern: JsonNode, actual: JsonNode, mustacheScope: Any?) {
    val config = MatcherConfig(randomArrayOrder = true)
    assertJsonMatchesInternal(mustacheScope, reason, pattern, actual, config)
}

private fun assertJsonMatchesInternal(
        mustacheScope: Any?,
        reason: String?,
        pattern: JsonNode,
        actual: JsonNode,
        config: MatcherConfig = DefaultMatcherConfig
) {
    JsonMatcher(config, mustacheScope).assertMatches(reason, pattern, actual)
}

/**
 * Returns the given paramValue string as JSON node if valid or throws an exception if not.
 *
 * @param paramValue The string to parse as JSON.
 * @param paramName The name of the param to inlcude in the exception message.
 * @throws IllegalArgumentException If the string cannot be parsed as JSON (invalid, ...).
 */
private fun parseAndValidate(paramValue: String, paramName: String): JsonNode {
    try {
        return convertToJsonNode(paramValue)
    } catch (ioe: IOException) {
        throw IllegalArgumentException("Failed to parse $paramName as JSON:\n$paramValue", ioe)
    }
}
