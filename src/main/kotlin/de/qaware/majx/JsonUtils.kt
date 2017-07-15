import com.fasterxml.jackson.databind.JsonNode
import de.qaware.majx.createObjectMapper
import java.io.IOException

private val JSON_MAPPER = createObjectMapper()

/**
 * Convert the json node into a string.
 *
 * @param object Json node that should be converted
 * @return Stringified version
 */
fun convertToString(`object`: JsonNode): String {
    try {
        return JSON_MAPPER.writeValueAsString(`object`)
    } catch (e: IOException) {
        throw IllegalArgumentException("Failed to serialize JSON", e)
    }

}

/**
 * Convert the string into a json node.
 *
 * @param s String that should be converted
 * @return Converted object
 */
fun convertToJsonNode(s: String): JsonNode {
    try {
        return JSON_MAPPER.readTree(s)
    } catch (e: IOException) {
        throw IllegalArgumentException("Failed to parse JSON", e)
    }
}
