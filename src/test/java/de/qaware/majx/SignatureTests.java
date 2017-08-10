package de.qaware.majx;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

public class SignatureTests {

    @Test
    public void testSignaturesWithoutMustacheScope() throws Exception {
        String reason = "Upps, something went wrong";
        String pattern = "{ \"expected\" : 1337, \"...\" : \"...\" }";
        String actual = "{ \"expected\" : 1337 }";

        JsonNode patternNode = JsonUtilsKt.convertToJsonNode(pattern);
        JsonNode actualNode = JsonUtilsKt.convertToJsonNode(actual);

        Majx.assertJsonMatches(pattern, actual);
        Majx.assertJsonMatches(patternNode, actualNode);
        Majx.assertJsonMatches(pattern, actualNode);
        Majx.assertJsonMatches(patternNode, actual);
        Majx.assertJsonMatches(pattern, actual, null);
        Majx.assertJsonMatches(pattern, actual, null);
        Majx.assertJsonMatches(patternNode, actualNode, null);
        Majx.assertJsonMatches(reason, pattern, actual, null);
        Majx.assertJsonMatches(null, pattern, actual, null);
        Majx.assertJsonMatches(reason, patternNode, actualNode, null);
        Majx.assertJsonMatches(null, patternNode, actualNode, null);
    }

    @Test
    public void testSignaturesWithMustacheScope() throws Exception {
        String reason = "Upps, something went wrong";
        String pattern = "{ \"myMustache\" : \"{{foo}}/baz\", \"...\" : \"...\" }";
        String actual = "{ \"myMustache\" : \"bar/baz\" }";

        JsonNode patternNode = JsonUtilsKt.convertToJsonNode(pattern);
        JsonNode actualNode = JsonUtilsKt.convertToJsonNode(actual);

        Map<String, String> mustacheScope = ImmutableMap.<String, String>builder()
                .put("foo", "bar")
                .build();

        Majx.assertJsonMatches(pattern, actual, mustacheScope);
        Majx.assertJsonMatches(pattern, actual, mustacheScope);
        Majx.assertJsonMatches(patternNode, actualNode, mustacheScope);
        Majx.assertJsonMatches(reason, pattern, actual, mustacheScope);
        Majx.assertJsonMatches(null, pattern, actual, mustacheScope);
        Majx.assertJsonMatches(reason, patternNode, actualNode, mustacheScope);
        Majx.assertJsonMatches(null, patternNode, actualNode, mustacheScope);
    }
}
