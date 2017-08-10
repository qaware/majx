/*
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
