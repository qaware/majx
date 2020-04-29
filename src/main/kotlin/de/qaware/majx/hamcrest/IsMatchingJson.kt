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

}

fun matchesJson(pattern: String): Matcher<String> {
    return IsMatchingJson(pattern)
}
