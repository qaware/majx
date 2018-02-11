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

import com.github.mustachejava.DefaultMustacheFactory
import java.io.StringReader
import java.io.StringWriter

/**
 * Provides common functionality dealing with mustache expressions.
 */
object MustacheMatcher {

    /**
     * Evaluate the mustache expression.
     *
     * @param expression    Expression to evaluate. If this is not a mustache expression (doesn't contain braces) we
     *                       just return it.
     * @param mustacheScope Scope for evaluation
     * @return Evaluated string
     */
    fun evaluateMustache(expression: String, mustacheScope: Any): String {
        // This is potentially a mustache expression. We don't start the mustache parser unless we know that there might
        // be a mustache expression to improve performance.
        if (potentiallyMustache(expression)) {
            // We could set this as a static variable (it is thread safe) but it just does a bit of caching that we
            // don't need
            val mf = DefaultMustacheFactory()
            val compiledPattern = mf.compile(StringReader(expression), "temporaryExpression")
            // Create output stream and evaluate mustache expression
            val writer = StringWriter()
            compiledPattern.execute(writer, mustacheScope)
            return writer.toString()
        } else {
            return expression
        }
    }

    /**
     * Assert that the actual value is equal to the pattern after the pattern is evaluated as a mustache expression.

     * @param pattern       Pattern that may be a mustache expression
     * @param actual        Actual value
     * @param mustacheScope Scope for evaluation. May be null to deactivate mustache evaluation.
     * @param location The location info (absolute path from root).
     * @param validationErrors List of validation errors to add to.
     */
    fun validate(pattern: String,
                 actual: String,
                 mustacheScope: Any?,
                 location: String,
                 validationErrors: MutableList<ValidationError>) {
        if (mustacheScope != null && potentiallyMustache(pattern)) {
            val computedPattern = evaluateMustache(pattern, mustacheScope)
            if (actual != computedPattern) {
                validationErrors.add(
                        ValidationError(
                                """Expected $location to be "$computedPattern" but it was "$actual"
                                  |        Pattern was evaluated as mustache expression
                                  |        Original pattern: "$pattern"""".trimMargin()
                        ))
            }
        } else if (actual != pattern) { // No mustache expression -> normal equals validation
            validationErrors.add(
                    ValidationError(
                            "Expected $location to be \"$pattern\" but it was \"$actual\""
                    ))
        }
    }

    /**
     * @param expression Expression to look at
     * @return true if this is potentially a mustache expression. false if this is definitely not a mustache expression
     */
    private fun potentiallyMustache(expression: String): Boolean {
        return expression.contains("{{") && expression.contains("}}")
    }
}
