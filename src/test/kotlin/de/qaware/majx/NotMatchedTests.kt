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

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Test
import kotlin.test.fail

class NotMatchedTests {

    @Test
    fun testSingleNotMatchedValueInObject() {
        val actual = readFile("notMatchedValue/singleNotMatched/object/actual.json")
        val pattern = readFile("notMatchedValue/singleNotMatched/object/pattern.json")
        val expectedMessage = readFile("notMatchedValue/singleNotMatched/object/expectedMessage.txt")

        try {
            assertJsonMatches(actual, pattern)
            fail()
        } catch (ex: AssertionError) {
            MatcherAssert.assertThat(ex.message, Matchers.`is`(expectedMessage))
        }
    }

    @Test
    fun testSingleNotMatchedValueInArray() {
        val actual = readFile("notMatchedValue/singleNotMatched/array/actual.json")
        val pattern = readFile("notMatchedValue/singleNotMatched/array/pattern.json")
        val expectedMessage = readFile("notMatchedValue/singleNotMatched/array/expectedMessage.txt")

        try {
            assertJsonMatches(actual, pattern)
            fail()
        } catch (ex: AssertionError) {
            MatcherAssert.assertThat(ex.message, Matchers.`is`(expectedMessage))
        }
    }
}