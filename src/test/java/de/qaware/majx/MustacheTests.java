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

import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static de.qaware.majx.TestSupportKt.readFile;


@RunWith(Parameterized.class)
public class MustacheTests {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        "success: mixed use with Map<String,String> mustache scope",
                        "mustache/success/actual.json",
                        "mustache/success/pattern.json",
                        ImmutableMap.<String, String>builder()
                                .put("mustacheVar1", "mustacheValue1")
                                .put("baseUrl", "https://base.com")
                                .build(),
                        null
                },
                {
                        "success: mixed use with POJO mustache scope",
                        "mustache/success/actual.json",
                        "mustache/success/pattern.json",
                        new MustacheScopePojo("mustacheValue1", "https://base.com"),
                        null
                },
                {
                        "error: value mismatch with Map<String,String> mustache scope",
                        "mustache/errorValueMismatch/mapMustacheScope/actual.json",
                        "mustache/errorValueMismatch/mapMustacheScope/pattern.json",
                        ImmutableMap.<String, String>builder()
                                .put("baseUrl", "https://base.com")
                                .build(),
                        "mustache/errorValueMismatch/mapMustacheScope/expectedMessage.txt",
                },
                {
                        "error: value mismatch with POJO mustache scope",
                        "mustache/errorValueMismatch/pojoMustacheScope/actual.json",
                        "mustache/errorValueMismatch/pojoMustacheScope/pattern.json",
                        new MustacheScopePojo(null, "https://base.com"),
                        "mustache/errorValueMismatch/pojoMustacheScope/expectedMessage.txt",
                }
        });
    }

    private String actualPath;
    private String patternPath;
    private Object mustacheScope;
    private String expectedMessagePath;

    public MustacheTests(String testName,
                         String actualPath,
                         String patternPath,
                         Object mustacheScope,
                         String expectedMessagePath
    ) {
        this.actualPath = actualPath;
        this.patternPath = patternPath;
        this.mustacheScope = mustacheScope;
        this.expectedMessagePath = expectedMessagePath;
    }

    @Test
    public void test() throws IOException {
        String actual = readFile(this.actualPath);
        String pattern = readFile(this.patternPath);
        if (this.expectedMessagePath != null) {
            String expectedMessage = readFile(this.expectedMessagePath);
            exception.expect(AssertionError.class);
            exception.expectMessage(expectedMessage);
        }
        Majx.assertJsonMatches(pattern, actual, this.mustacheScope);
    }

    private static class MustacheScopePojo {
        private String mustacheVar1;
        private String baseUrl;

        MustacheScopePojo(String mustacheVar1, String baseUrl) {
            this.mustacheVar1 = mustacheVar1;
            this.baseUrl = baseUrl;
        }

        public String getMustacheVar1() {
            return mustacheVar1;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        @Override
        public String toString() {
            return "MustacheScopePojo{" +
                    "mustacheVar1='" + mustacheVar1 + '\'' +
                    ", baseUrl='" + baseUrl + '\'' +
                    '}';
        }
    }
}
