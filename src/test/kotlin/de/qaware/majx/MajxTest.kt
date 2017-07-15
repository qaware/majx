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

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test
import kotlin.test.fail

class MajxTest {

    @Test
    fun testEmptyObjectsSuccess() {
        val actual = readFile("exact/empty/object/success/actual.json")
        val pattern = readFile("exact/empty/object/success/pattern.json")

        assertJsonMatches(actual, pattern)
    }

    @Test
    fun testUnexpectedInObject() {
        val actual = readFile("exact/empty/object/error/unexpected/actual.json")
        val pattern = readFile("exact/empty/object/error/unexpected/pattern.json")
        val expectedMessage = readFile("exact/empty/object/error/unexpected/expectedMessage.txt")

        try {
            assertJsonMatches(actual, pattern)
            fail()
        } catch (ex: AssertionError) {
            assertThat(ex.message, `is`(expectedMessage))
        }
    }

    @Test
    fun testMissingInObject() {
        val actual = readFile("exact/empty/object/error/missing/actual.json")
        val pattern = readFile("exact/empty/object/error/missing/pattern.json")
        val expectedMessage = readFile("exact/empty/object/error/missing/expectedMessage.txt")

        try {
            assertJsonMatches(actual, pattern)
            fail()
        } catch (ex: AssertionError) {
            assertThat(ex.message, `is`(expectedMessage))
        }
    }

    @Test
    fun testEmptyArraysSuccess() {
        val actual = readFile("exact/empty/array/actual.json")
        val pattern = readFile("exact/empty/array/pattern.json")

        assertJsonMatches(actual, pattern)
    }

    @Test
    fun testMustacheMatch() {
        val actual = readFile("mustache/actual.json")
        val pattern = readFile("mustache/pattern.json")

        assertJsonMatches(actual, pattern, mapOf(
                Pair("mustacheVar1", "mustacheValue1"),
                Pair("mustacheVar2", "mustacheValue2")
        ))
    }
}
