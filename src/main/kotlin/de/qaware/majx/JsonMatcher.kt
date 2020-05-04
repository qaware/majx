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
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

/**
 * Matcher that compares an actual JSON with a pattern JSON object.
 *
 * Can use templates on strings to check against dynamic test expectations.
 * Accepts wildcards.
 *
 * @property config        The config that controls certain matcher aspects.
 * @property mustacheScope The scope from which the mustache parser reads it variables. May be a map or a POJO.
 *                         This is used in case we need dynamic test expectations.
 * @constructor Creates a new [JsonMatcher] with the given [mustacheScope].
 */
class JsonMatcher(private val config: MatcherConfig, private val mustacheScope: Any?) {

    /**
     * Companion object provides support methods for [JsonMatcher].
     */
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
         *
         * @param pattern      Pattern object.
         * @param actual       Actual value.
         * @param locationInfo Location information for error output.
         * @param <T>          Type of container to check.
         */
        private fun <T : ContainerNode<T>> validateCorrectSize(pattern: ContainerNode<T>,
                                                               actual: ContainerNode<T>,
                                                               locationInfo: String) {
            if (containsWildcard(pattern)) {
                // Wildcard: Number of elements must be greater or equal to the number of actually specified elements
                val specifiedElems = pattern.size() - 1
                val actualContainerType = if (actual.isArray()) "array" else "object"
                assertThat<Int>("${locationInfo}Actual $actualContainerType size too small",
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

                    val notMatchedSet = expectedPropertiesSet.union(actualPropertiesSet).minus(
                            expectedPropertiesSet.intersect(actualPropertiesSet)
                    )
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
     * @param pattern Pattern object.
     * @param actual   Actual value.
     */
    fun assertMatches(reason: String?, pattern: JsonNode, actual: JsonNode) {
        try {
            validate(pattern, actual, "$")
        } catch (ex: AssertionError) {
            val actualAsText = convertToString(actual)
            val expectedAsText = convertToString(pattern)

            val mustacheScopeString = if (this.mustacheScope != null) {
                """

                |--------------------------------------------------------------------------------------------
                |Mustache Scope
                |--------------------------------------------------------------------------------------------
                |${printMustacheScope(this.mustacheScope)}
                """
            } else ""

            val reasonOutput: String = if (reason != null) "$reason: " else ""
            throw AssertionError("""$reasonOutput${ex.message}.

                |--------------------------------------------------------------------------------------------
                |Actual JSON
                |--------------------------------------------------------------------------------------------
                |$actualAsText

                |--------------------------------------------------------------------------------------------
                |Pattern
                |--------------------------------------------------------------------------------------------
                |$expectedAsText$mustacheScopeString""".trimMargin(), ex)
        }
    }

    private fun printMustacheScope(mustacheScope: Any): String {
        val builder = StringBuilder()
        if (mustacheScope is Map<*, *>) {
            val longestKey = mustacheScope.keys.fold(0, { acc, elem -> Math.max(acc, (elem as String).length) })
            mustacheScope.forEach { key, value ->
                builder.appendln("${key.toString().padEnd(longestKey + 1)}= $value")
            }
        } else {
            builder.appendln(mustacheScope.toString())
        }

        return builder.toString()
    }

    /**
     * Recursively validate that the actual value matches the pattern object (potentially with wildcards)
     *
     * @param pattern       Pattern object.
     * @param actual        Actual value.
     * @param attributeName Name of currently processed attribute (absolut path from root).
     */
    private fun validate(pattern: JsonNode, actual: JsonNode, attributeName: String) {
        if (isWildcard(pattern)) {
            return
        }

        val locationInfo = formatLocation(attributeName)
        assertThat(locationInfo + "Incorrect type of attribute",
                actual.nodeType, `is`(pattern.nodeType))

        when {
            pattern is ObjectNode && actual is ObjectNode -> validateObject(pattern, actual, attributeName)
            pattern is ArrayNode && actual is ArrayNode -> validateArray(pattern, actual, attributeName)
            pattern is TextNode && actual is TextNode -> validateString(pattern, actual, attributeName)
            pattern is ValueNode && actual is ValueNode -> validateScalar(pattern, actual, attributeName)
            else -> {
                val error = "Incompatible types in actual and expected. " +
                        "Type of actual: ${actual.javaClass}, " +
                        "type of expected: ${pattern.javaClass}"
                throw AssertionError("$locationInfo$error")
            }
        }
    }

    /**
     * Recursively validate that the actual value matches the pattern object (potentially with wildcards).
     *
     * @param pattern       Pattern object.
     * @param actual        Actual value.
     * @param attributeName Name of currently processed attribute (absolut path from root).
     */
    private fun validateObject(pattern: ObjectNode, actual: ObjectNode, attributeName: String) {
        val locationInfo = formatLocation(attributeName)
        validateCorrectSize<ObjectNode>(pattern, actual, locationInfo)

        val expectedFieldNames = pattern.fieldNames()
        while (expectedFieldNames.hasNext()) {
            val expectedFieldName = expectedFieldNames.next()
            if (WILDCARD == expectedFieldName && isWildcard(pattern.get(WILDCARD))) {
                continue
            }
            assertThat("$locationInfo Expected field name '$expectedFieldName' not found.",
                    actual.get(expectedFieldName), notNullValue())
            validate(pattern.get(expectedFieldName), actual.get(expectedFieldName),
                    "$attributeName.$expectedFieldName")
        }
    }

    /**
     * Recursively validate that the actual value matches the pattern array (potentially with wildcards).
     *
     * @param pattern       Pattern array.
     * @param actual        Actual array.
     * @param attributeName Name of currently processed attribute (absolute path from root).
     */
    private fun validateArray(pattern: ArrayNode, actual: ArrayNode, attributeName: String) {
        val locationInfo: String = formatLocation(attributeName)
        validateCorrectSize(pattern, actual, locationInfo)

        if (config.randomArrayOrder) {
            validateArrayRandom(attributeName, pattern, actual)
        } else {
            validateArrayOrdered(attributeName, pattern, actual)
        }
    }

    /**
     * Recursively validate that the actual value matches the pattern array in any order (potentially with wildcards).
     *
     * @param pattern       Pattern array.
     * @param actual        Actual array.
     * @param attributeName Name of currently processed attribute (absolute path from root).
     */
    private fun validateArrayRandom(attributeName: String, pattern: ArrayNode, actual: ArrayNode) {
        val locationInfo: String = formatLocation(attributeName)

        val wildcardMatchMode = containsWildcard(pattern)

        val actualNodes: List<JsonNode> = actual.toList()
        val expectedNodes: List<JsonNode> = if (wildcardMatchMode) pattern.filterNot(::isWildcard) else pattern.toList()

        // For each element in expected find at least one element in actual that does not fail validation
        expectedNodes.forEach { expectedNode ->
            if (!hasItem(actualNodes, expectedNode)) {
                if (wildcardMatchMode) {
                    // Wildcard found -> actual must contain all pattern elements (and may contain additional elements)
                    throw AssertionError("$locationInfo Actual array does not contain all pattern " +
                            "array elements ignoring order")
                } else {
                    // No wildcard -> sets must be equal
                    throw AssertionError("$locationInfo Arrays are not equal ignoring order")
                }
            }
        }
    }

    /**
     * Returns whether the given list contains at least one item that matches the given pattern.
     *
     * @param list The list.
     * @param pattern The pattern.
     * @return Whether the list contains an item that matches the given pattern.
     */
    private fun hasItem(list: List<JsonNode>, pattern: JsonNode): Boolean {
        for (actualNode in list) {
            // I know it is bad practice to use exeptions for control flow but currently the validation works
            // this way. When we restructure the code to return a list of validation errors instead of throwing, this
            // function will become cleaner.
            try {
                validate(pattern, actualNode, "ignored")
                return true
            } catch (ignored: AssertionError) {
                // Ignored
            }
        }
        return false
    }

    /**
     * Recursively validate that the actual value matches the pattern array in fixed order (potentially with wildcards).
     *
     * The fixed order is dictated by the pattern.
     *
     * @param pattern       Pattern array.
     * @param actual        Actual array.
     * @param attributeName Name of currently processed attribute (absolute path from root).
     */
    private fun validateArrayOrdered(attributeName: String, pattern: ArrayNode, actual: ArrayNode) {
        // If pattern contains wildcard only the elements up to the wildcard must match.
        val maxIndex = if (containsWildcard(pattern)) pattern.size() - 2 else pattern.size() - 1
        for (i in 0..maxIndex) {
            validate(pattern.get(i), actual.get(i), "$attributeName[$i]")
        }
    }

    /**
     * Validate string match, potentially using mustache template engine to replace variable parts of the string
     *
     * @param pattern       Pattern object.
     * @param actual        Actual value.
     * @param attributeName Name of currently processed attribute (absolut path from root).
     */
    private fun validateString(pattern: TextNode, actual: TextNode, attributeName: String) {
        val locationInfo = formatLocation(attributeName)
        val patternText = pattern.textValue()
        MustacheMatcher.assertEqual(locationInfo + "Value does not match", patternText, actual.textValue(),
                mustacheScope)
    }

    /**
     * Recursively validate that the actual value matches the pattern object (potentially with wildcards).
     *
     * @param pattern       Pattern object.
     * @param actual        Actual value.
     * @param attributeName Name of curretly processed attribute (absolut path from root).
     */
    private fun validateScalar(pattern: ValueNode, actual: ValueNode, attributeName: String) {
        val locationInfo = formatLocation(attributeName)
        assertThat(locationInfo + "Element does not match", actual.asText(), `is`<String>(pattern.asText()))
    }
}
