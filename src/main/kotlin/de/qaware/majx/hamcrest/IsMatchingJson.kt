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
package de.qaware.majx.hamcrest

import de.qaware.majx.assertJsonMatches
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * This is a simple wrapper around Majx, making it usable as a Hamcrest matcher.
 */
class IsMatchingJson constructor(private val pattern: String) : TypeSafeMatcher<String>() {

    override fun describeTo(description: Description?) {
        description?.appendText("matches JSON $pattern")
    }

    override fun matchesSafely(item: String?): Boolean {
        val checkedItem = item ?: throw IllegalArgumentException("Failed to parse JSON: given parameter was null")
        // This is admittedly not the prettiest of solutions, but it keeps the original internal implementation
        // of Majx intact. Majx uses validations for all checks while Hamcrest needs boolean returns. This means
        // that returning a boolean from Majx would require refactoring all checks to return a boolean and then
        // throwing an AssertionError once a check returns false. This makes the main code of Majx harder to read,
        // so this seems like the lesser of two evils.
        try {
            assertJsonMatches(pattern, checkedItem)
        } catch (e: AssertionError) {
            return false
        }
        return true
    }

    override fun describeMismatchSafely(item: String?, mismatchDescription: Description?) {
        val checkedItem = item ?: return
        try {
            assertJsonMatches(pattern, checkedItem)
        } catch (e: AssertionError) {
            mismatchDescription?.appendText(e.message)
        }
    }
}

fun matchesJson(pattern: String): Matcher<String> {
    return IsMatchingJson(pattern)
}
