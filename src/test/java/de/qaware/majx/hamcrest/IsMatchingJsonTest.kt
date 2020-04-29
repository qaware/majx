package de.qaware.majx.hamcrest

import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test



internal class IsMatchingJsonTest {

    @Test
    fun matchesSafely() {
        assertThat("{ \"foo\" : \"bar\" }", matchesJson("{ \"foo\" : \"...\" }"))
    }
}
