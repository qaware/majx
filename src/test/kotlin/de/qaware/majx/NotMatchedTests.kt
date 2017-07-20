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