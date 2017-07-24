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
 * Asserts that the given actual JSON node matches the given JSON pattern node by evaluating
 * mustache expressions with the given mustache scope.
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
private fun assertJsonMatches(reason: String?, pattern: JsonNode, actual: JsonNode, mustacheScope: Any?) =
        JsonMatcher(mustacheScope).assertMatches(reason, pattern, actual)

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
