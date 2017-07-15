package de.qaware.majx

import org.junit.Test

class MajxTest {

    @Test
    fun testEmptyObject() {
        val actual = readFile("exact/empty/object/actual.json")
        val pattern = readFile("exact/empty/object/pattern.json")

        assertJsonMatches(null, actual, pattern, null)
    }

    @Test
    fun testEmptyArray() {
        val actual = readFile("exact/empty/array/actual.json")
        val pattern = readFile("exact/empty/array/pattern.json")

        assertJsonMatches(null, actual, pattern, null)
    }

    @Test
    fun testMustacheMatch() {
        val actual = readFile("mustache/actual.json")
        val pattern = readFile("mustache/pattern.json")

        assertJsonMatches(null, actual, pattern, mapOf(
                Pair("mustacheVar1", "mustacheValue1"),
                Pair("mustacheVar2", "mustacheValue2")
        ))
    }
}
