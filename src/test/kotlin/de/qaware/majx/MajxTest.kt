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
