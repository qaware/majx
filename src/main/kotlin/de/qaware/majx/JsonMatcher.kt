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
import com.fasterxml.jackson.databind.node.*
import com.google.common.collect.Sets
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

/**
 * Matcher that compares an actual JSON with a pattern JSON object.
 *
 * Can use templates on strings to check against dynamic test expectations.
 * Accepts wildcards.
 *
 * @param mustacheScope The scope from which the mustache parser reads it variables. May be a map or a POJO. This is
 *                      used in case we need dynamic test expectations.
 */
class JsonMatcher(val mustacheScope: Any?) {

    companion object {

        /**
         * Identifier for a wildcard.
         *
         * A wildcard can be used in patterns if the content of the actual key or value is unimportant.
         */
        private const val WILDCARD = "..."

        /**
         * Format location information string from attribute name
         *
         * @param attributeName Name of the attribute.
         * @return The formatted location.
         */
        private fun formatLocation(attributeName: String): String = "Error at location $attributeName: "

        /**
         * @param node Node to check
         * @return True if the node contains a wildcard.
         */
        private fun isWildcard(node: JsonNode): Boolean {
            return node.isTextual && WILDCARD == node.textValue()
        }

        /**
         * @param iterator The iterator to initialize the set with.
         * @return A new [MutableSet] containing the elements of the given iterator.
         */
        private fun asSet(iterator: Iterator<String>): MutableSet<String> {
            val result = LinkedHashSet<String>()
            iterator.forEach { result.add(it) }
            return result
        }


        /**
         * Check if the node contains a wildcard.
         *
         * The wildcard is the last entry in the array or the child element with the
         * name "..." in an object.

         * @param node Node to check
         * @return True if the node contains a wildcard
         */
        private fun containsWildcard(node: JsonNode): Boolean {
            return when (node) {
                is ArrayNode -> node.size() > 0 && isWildcard(node.get(node.size() - 1))
                is ObjectNode -> node.get(WILDCARD) != null && isWildcard(node.get(WILDCARD))
                else -> throw IllegalArgumentException("Only array and object nodes can contain wildcards.")
            }
        }

        /**
         * Validate that array or object sizes are correct. If there is a wildcard in the pattern object this means that the
         * actual object may contain more elements than the pattern object.

         * @param actual       Actual value
         * @param pattern      Pattern object
         * @param locationInfo Location information for error output
         * @param <T>          Type of container to check
         */
        private fun <T : ContainerNode<T>> validateCorrectSize(actual: ContainerNode<T>,
                                                               pattern: ContainerNode<T>,
                                                               locationInfo: String) {
            if (containsWildcard(pattern)) {
                // Wildcard: Number of elements must be greater or equal to the number of actually specified elements
                val specifiedElems = pattern.size() - 1
                assertThat<Int>("${locationInfo}Actual array too short",
                        actual.size(),
                        greaterThanOrEqualTo<Int>(specifiedElems))
            } else {
                // No wildcard: number of elements must match exactly
                val errorMsg: String
                if (actual.isArray) {
                    errorMsg = "${locationInfo}Sizes of arrays do not match."
                } else if (actual.isObject) {
                    val expectedPropertiesSet = asSet(pattern.fieldNames())
                    val actualPropertiesSet = asSet(actual.fieldNames())
                    val notMatchedSet = Sets.difference(
                            Sets.union<String>(expectedPropertiesSet, actualPropertiesSet),
                            Sets.intersection<String>(expectedPropertiesSet, actualPropertiesSet))
                    val notMatched = if (notMatchedSet.isNotEmpty()) notMatchedSet.joinToString() else "(empty)"
                    val expectedProperties = if (expectedPropertiesSet.isNotEmpty())
                        expectedPropertiesSet.joinToString() else "(empty)"
                    val actualProperties = if (actualPropertiesSet.isNotEmpty())
                        actualPropertiesSet.joinToString() else "(empty)"

                    errorMsg = """${locationInfo}Size of object properties does not match.
                                    |Expected properties:       $expectedProperties
                                    |Actual properties:         $actualProperties
                                    |Not matched properties:    $notMatched""".trimMargin()
                } else {
                    throw UnsupportedOperationException("Not implemented handling of subtype '${actual.javaClass.name}' " +
                            "of type '${ContainerNode<*>::javaClass.name}'")
                }

                assertThat<Int>(errorMsg, actual.size(), equalTo<Int>(pattern.size()))
            }
        }
    }


    /**
     * Recursively validate that the actual JSON matches the pattern JSON (potentially with wildcards). Uses default
     * root.

     * @param reason  The error message to prepend to the JSON matcher error message if validation fails.
     * @param actual   Actual value.
     * @param expected Pattern object.
     */
    fun assertMatches(reason: String?, actual: JsonNode, expected: JsonNode) {
        try {
            validate(actual, expected, "$")
        } catch (ex: AssertionError) {
            val actualAsText = convertToString(actual)
            val expectedAsText = convertToString(expected)
            throw AssertionError("""${reason ?: ""}${ex.message}.

                |--------------------------------------------------------------------------------------------
                |Actual JSON
                |--------------------------------------------------------------------------------------------
                |$actualAsText

                |--------------------------------------------------------------------------------------------
                |Pattern
                |--------------------------------------------------------------------------------------------
                |$expectedAsText
                """.trimMargin(), ex)
        }
    }

    /**
     * Recursively validate that the actual value matches the pattern object (potentially with wildcards)

     * @param actual        Actual value
     * @param pattern       Pattern object
     * @param attributeName Name of currently processed attribute (absolut path from root)
     */
    private fun validate(actual: JsonNode, pattern: JsonNode, attributeName: String) {
        if (isWildcard(pattern)) {
            return
        }

        val locationInfo = formatLocation(attributeName)
        assertThat<JsonNodeType>(locationInfo + "Incorrect type of attribute", actual.nodeType, `is`<JsonNodeType>(pattern.nodeType))

        when {
            pattern is ObjectNode && actual is ObjectNode -> validateObject(actual, pattern, attributeName)
            pattern is ArrayNode && actual is ArrayNode -> validateArray(actual, pattern, attributeName)
            pattern is TextNode && actual is TextNode -> validateString(actual, pattern, attributeName)
            pattern is ValueNode && actual is ValueNode -> validateScalar(actual, pattern, attributeName)
            else -> {
                val error = "Incompatible types in actual and expected. " +
                        "Type of actual: ${actual.javaClass.toString()}, " +
                        "type of expected: ${pattern.javaClass.toString()}"
                throw AssertionError("$locationInfo$error")
            }
        }
    }

    /**
     * Recursively validate that the actual value matches the pattern object (potentially with wildcards).

     * @param actual        Actual value
     * @param pattern       Pattern object
     * @param attributeName Name of currently processed attribute (absolut path from root)
     */
    private fun validateObject(actual: ObjectNode, pattern: ObjectNode, attributeName: String) {
        val locationInfo = formatLocation(attributeName)
        validateCorrectSize<ObjectNode>(actual, pattern, locationInfo)

        val expectedFieldNames = pattern.fieldNames()
        while (expectedFieldNames.hasNext()) {
            val expectedFieldName = expectedFieldNames.next()
            if (WILDCARD == expectedFieldName && isWildcard(pattern.get(WILDCARD))) {
                continue
            }
            assertThat<JsonNode>("${locationInfo} Expected field name '$expectedFieldName' not found.",
                    actual.get(expectedFieldName), notNullValue())
            validate(actual.get(expectedFieldName), pattern.get(expectedFieldName),
                    "$attributeName.$expectedFieldName")
        }
    }

    /**
     * Recursively validate that the actual value matches the pattern object (potentially with wildcards).

     * @param actual        Actual value
     * @param pattern       Pattern object
     * @param attributeName Name of currently processed attribute (absolut path from root)
     */
    private fun validateArray(actual: ArrayNode, pattern: ArrayNode, attributeName: String) {
        val locationInfo = formatLocation(attributeName)
        validateCorrectSize(actual, pattern, locationInfo)
        // Special case: If the pattern has only the wildcard element, any array matches it.
        if (pattern.size() == 1 && containsWildcard(pattern)) {
            return
        }
        for (i in 0..pattern.size() - 1) {
            validate(actual.get(i), pattern.get(i), "$attributeName[$i]")
        }
    }

    /**
     * Validate string match, potentially using mustache template engine to replace variable parts of the string

     * @param actual        Actual value
     * @param pattern       Pattern object
     * @param attributeName Name of currently processed attribute (absolut path from root)
     */
    private fun validateString(actual: TextNode, pattern: TextNode, attributeName: String) {
        val locationInfo = formatLocation(attributeName)
        val patternText = pattern.textValue()
        MustacheMatcher.assertEqual(locationInfo + "Value does not match", patternText, actual.textValue(),
                mustacheScope)
    }

    /**
     * Recursively validate that the actual value matches the pattern object (potentially with wildcards).

     * @param actual        Actual value
     * @param pattern       Pattern object
     * @param attributeName Name of currently processed attribute (absolut path from root)
     */
    private fun validateScalar(actual: ValueNode, pattern: ValueNode, attributeName: String) {
        val locationInfo = formatLocation(attributeName)
        assertThat<String>(locationInfo + "Element does not match", actual.asText(), `is`<String>(pattern.asText()))
    }
}
