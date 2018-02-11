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
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode

/**
 * Matcher that compares an actual JSON with a pattern JSON object.
 *
 * Can use templates on strings to check against dynamic test expectations.
 * Accepts wildcards.
 *
 * @property mustacheScope The scope from which the mustache parser reads it variables. May be a map or a POJO.
 *                         This is used in case we need dynamic test expectations.
 * @constructor Creates a new [JsonMatcher] with the given [mustacheScope].
 */
class JsonMatcher(private val mustacheScope: Any?) {

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
         * Prints the given set handling empty sets nicely.
         *
         * @param s The set to print, may be empty.
         */
        private fun printProperties(s: Set<String>) = if (s.isNotEmpty()) s.joinToString() else "(empty)"

        /**
         * Validate that array or object sizes are correct. If there is a wildcard in the pattern object this means that the
         * actual object may contain more elements than the pattern object.
         *
         * @param pattern      Pattern object.
         * @param actual       Actual value.
         * @param locationInfo Location information for error output.
         * @param <T>          Type of container to check.
         */
        private fun validateCorrectSize(pattern: ArrayNode,
                                        actual: ArrayNode,
                                        locationInfo: String,
                                        validationErrors: MutableList<ValidationError>) {
            if (containsWildcard(pattern)) {
                if (actual.size() < pattern.size() - 1) {
                    // Wildcard: Number of elements must be greater or equal to the number of actually specified elements
                    validationErrors.add(ValidationError("Expected array $locationInfo" +
                            " to have at least ${pattern.size() - 1} entries but it has only ${actual.size()} entries"))
                }
            } else if (actual.size() != pattern.size()) {
                // No wildcard: number of elements must match exactly
                validationErrors.add(ValidationError("Expected array $locationInfo to " +
                        "have exactly ${pattern.size()} entrie(s) but it has ${actual.size()} entrie(s)"))
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
        val validationErrors = mutableListOf<ValidationError>()
        val errors = validate(pattern, actual, "$", validationErrors)
        if (errors.isEmpty()) {
            // No validation errors -> success.
            return
        }

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
        val errorsOutput = errors.joinToString(separator = "\n    ") { validationError -> validationError.msg }
        throw AssertionError("""${reasonOutput}JSON validation failed.
                |
                |    $errorsOutput
                |
                |--------------------------------------------------------------------------------------------
                |Actual JSON
                |--------------------------------------------------------------------------------------------
                |$actualAsText

                |--------------------------------------------------------------------------------------------
                |Pattern
                |--------------------------------------------------------------------------------------------
                |$expectedAsText$mustacheScopeString""".trimMargin())
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
     * @param location Name of currently processed attribute (absolut path from root).
     */
    private fun validate(pattern: JsonNode,
                         actual: JsonNode, location: String,
                         validationErrors: MutableList<ValidationError>): List<ValidationError> {
        if (isWildcard(pattern)) {
            return emptyList<ValidationError>()
        }

        if (actual.nodeType != pattern.nodeType) {
            val expectedValueDisplay = printValueNodeWithPrefix(pattern)
            val actualValueDisplay = printValueNodeWithPrefix(actual)
            validationErrors.add(ValidationError(("Expected $location to be " +
                    "of type ${pattern.nodeType} ${expectedValueDisplay}but it was of type ${actual.nodeType} " +
                    actualValueDisplay).trim()))
            return validationErrors
        }

        when {
            pattern is ObjectNode && actual is ObjectNode -> validateObject(pattern, actual, location, validationErrors)
            pattern is ArrayNode && actual is ArrayNode -> validateArray(pattern, actual, location, validationErrors)
            pattern is TextNode && actual is TextNode -> validateString(pattern, actual, location, validationErrors)
            pattern is ValueNode && actual is ValueNode -> validateScalar(pattern, actual, location, validationErrors)
            else -> {
                throw IllegalArgumentException("Incompatible types in actual and expected. " +
                        "Type of actual: ${actual.javaClass.toString()}, " +
                        "type of expected: ${pattern.javaClass.toString()}")
            }
        }
        return validationErrors
    }

    private fun printValueNodeWithPrefix(node: JsonNode, prefix: String = "with value "): String {
        if (!node.isValueNode) {
            return ""
        }
        val textValueRaw = node.asText()
        if (node.isTextual) {
            val textPotentiallyShortened = if (textValueRaw.length > 30) {
                "\"${textValueRaw.substring(0, 26)}...\" (shortened)"
            } else "\"$textValueRaw\""

            return "$prefix$textPotentiallyShortened "
        }

        return "$prefix$textValueRaw "
    }

    /**
     * Recursively validate that the actual value matches the pattern object (potentially with wildcards).
     *
     * @param pattern       Pattern object.
     * @param actual        Actual value.
     * @param location Name of currently processed attribute (absolut path from root).
     */
    private fun validateObject(pattern: ObjectNode,
                               actual: ObjectNode,
                               location: String,
                               validationErrors: MutableList<ValidationError>) {

        val expectedFieldNames = pattern.fieldNames()
        while (expectedFieldNames.hasNext()) {
            val expectedFieldName = expectedFieldNames.next()
            val expectedValue = pattern.get(expectedFieldName)
            if (WILDCARD == expectedFieldName && isWildcard(pattern.get(WILDCARD))) {
                continue
            }
            val actualValue = actual.get(expectedFieldName)
            if (actualValue == null) {
                val expectedValueDisplay = printValueNodeWithPrefix(expectedValue)
                validationErrors.add(ValidationError(
                        "Expected object $location to have property " +
                                "\"$expectedFieldName\" ${expectedValueDisplay}but it didn't"
                ))
                continue
            }
            validate(expectedValue, actualValue, "$location.$expectedFieldName", validationErrors)
        }

        // If the pattern contains a wildcard property, we ignore additional properties in the
        // actual JSON without failing. That means we are done here.
        if (containsWildcard(pattern)) {
            return
        }

        val expectedProperties = asSet(pattern.fieldNames())
        val actualProperties = asSet(actual.fieldNames())
        val unexpectedProperties = actualProperties.minus(expectedProperties)

        unexpectedProperties.forEach { unexpected: String ->
            validationErrors.add(ValidationError(
                    "Expected object $location to NOT have property \"$unexpected\" but it did"
            ))
        }
    }

    /**
     * Recursively validate that the actual value matches the pattern object (potentially with wildcards).
     *
     * @param pattern       Pattern object.
     * @param actual        Actual value.
     * @param location Name of currently processed attribute (absolut path from root).
     */
    private fun validateArray(pattern: ArrayNode,
                              actual: ArrayNode,
                              location: String,
                              validationErrors: MutableList<ValidationError>) {
        validateCorrectSize(pattern, actual, location, validationErrors)

        // If pattern contains wildcard only the elements up to the wildcard must match.
        val maxIndex = if (containsWildcard(pattern)) pattern.size() - 2 else pattern.size() - 1
        for (i in 0..maxIndex) {
            val expectedValue = pattern.get(i)
            if (!actual.has(i)) {
                val expectedValueDisplay = printValueNodeWithPrefix(expectedValue, "")
                validationErrors.add(ValidationError(
                        "Expected $location[$i] to be ${expectedValueDisplay}but no entry exists at this index"
                ))
                continue
            }
            validate(expectedValue, actual.get(i), "$location[$i]", validationErrors)
        }


    }

    /**
     * Validate string match, potentially using mustache template engine to replace variable parts of the string
     *
     * @param pattern       Pattern object.
     * @param actual        Actual value.
     * @param location Name of currently processed attribute (absolut path from root).
     */
    private fun validateString(pattern: TextNode,
                               actual: TextNode,
                               location: String,
                               validationErrors: MutableList<ValidationError>) {
        val patternText = pattern.textValue()
        MustacheMatcher.validate(patternText, actual.textValue(),
                mustacheScope, location, validationErrors)
    }

    /**
     * Recursively validate that the actual value matches the pattern value.
     *
     * @param pattern       Pattern object.
     * @param actual        Actual value.
     * @param location Name of currently processed attribute (absolute path from root).
     * @param validationErrors List of validation errors to add to.
     */
    private fun validateScalar(pattern: ValueNode,
                               actual: ValueNode,
                               location: String,
                               validationErrors: MutableList<ValidationError>) {
        if (actual.asText() != pattern.asText()) {
            validationErrors.add(
                    ValidationError("Expected $location to be ${pattern.asText()} but it was ${actual.asText()}"))
        }
    }
}
