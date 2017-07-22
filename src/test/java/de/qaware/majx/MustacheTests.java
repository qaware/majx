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
import java.util.Map;

import static de.qaware.majx.TestSupportKt.readFile;


@RunWith(Parameterized.class)
public class MustacheTests {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        "exact match",
                        "mustache/actual.json",
                        "mustache/pattern.json",
                        ImmutableMap.<String, String>builder()
                                .put("mustacheVar1", "mustacheValue1")
                                .put("baseUrl", "https://base.com")
                                .build()
                }
        });
    }

    private String actualPath;
    private String patternPath;
    private Map<String, String> mustacheScope;

    public MustacheTests(String testName,
                         String actualPath,
                         String patternPath,
                         Map<String, String> mustacheScope) {
        this.actualPath = actualPath;
        this.patternPath = patternPath;
        this.mustacheScope = mustacheScope;
    }

    @Test
    public void test() throws IOException {
        String actual = readFile(this.actualPath);
        String pattern = readFile(this.patternPath);
        MajxKt.assertJsonMatches(actual, pattern, this.mustacheScope);
    }
}
