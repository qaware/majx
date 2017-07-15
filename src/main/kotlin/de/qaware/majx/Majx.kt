package de.qaware.majx

import com.fasterxml.jackson.databind.JsonNode
import convertToJsonNode

fun assertJsonMatches(reason: String?, actual: String, pattern: String, mustacheScope: Any?) =
        assertJsonMatches(reason,
                convertToJsonNode(actual),
                convertToJsonNode(pattern),
                mustacheScope)

fun assertJsonMatches(reason: String?, actual: JsonNode, pattern: JsonNode, mustacheScope: Any?) =
        JsonMatcher(mustacheScope).assertMatches(reason, actual, pattern)
