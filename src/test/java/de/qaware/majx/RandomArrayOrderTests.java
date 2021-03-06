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

import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static de.qaware.majx.TestSupportKt.readFile;
import static org.junit.Assert.assertThrows;


@RunWith(Parameterized.class)
public class RandomArrayOrderTests {

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        "No wildcard | Success | with primitive elements",
                        "randomArrayOrder/noWildcard/success/primitive/actual.json",
                        "randomArrayOrder/noWildcard/success/primitive/pattern.json",
                        null
                },
                {
                        "No wildcard | Success | Nested wildcards",
                        "randomArrayOrder/noWildcard/success/nestedWildcard/actual.json",
                        "randomArrayOrder/noWildcard/success/nestedWildcard/pattern.json",
                        null
                },
                {
                        "No wildcard | error | expected array element not matched",
                        "randomArrayOrder/noWildcard/error/notMatchedValue/actual.json",
                        "randomArrayOrder/noWildcard/error/notMatchedValue/pattern.json",
                        "randomArrayOrder/noWildcard/error/notMatchedValue/expectedMessage.txt"
                },
                {
                        "No wildcard | error | unexpected array element",
                        "randomArrayOrder/noWildcard/error/unexpected/actual.json",
                        "randomArrayOrder/noWildcard/error/unexpected/pattern.json",
                        "randomArrayOrder/noWildcard/error/unexpected/expectedMessage.txt"
                },
                {
                        "No wildcard | error | missing array element",
                        "randomArrayOrder/noWildcard/error/missing/actual.json",
                        "randomArrayOrder/noWildcard/error/missing/pattern.json",
                        "randomArrayOrder/noWildcard/error/missing/expectedMessage.txt"
                },
                {
                        "With wildcard | Success | same length",
                        "randomArrayOrder/wildcard/success/exact/actual.json",
                        "randomArrayOrder/wildcard/success/exact/pattern.json",
                        null
                },
                {
                        "With wildcard | Success | with additional elements",
                        "randomArrayOrder/wildcard/success/withAdditionalElements/actual.json",
                        "randomArrayOrder/wildcard/success/withAdditionalElements/pattern.json",
                        null
                },
                {
                        "With wildcard | error | unexpected array element",
                        "randomArrayOrder/wildcard/error/unexpected/actual.json",
                        "randomArrayOrder/wildcard/error/unexpected/pattern.json",
                        "randomArrayOrder/wildcard/error/unexpected/expectedMessage.txt"
                },
                {
                        "With wildcard | error | missing array element",
                        "randomArrayOrder/wildcard/error/missing/actual.json",
                        "randomArrayOrder/wildcard/error/missing/pattern.json",
                        "randomArrayOrder/wildcard/error/missing/expectedMessage.txt"
                },

        });
    }

    private final String actualPath;
    private final String patternPath;
    private final String expectedMessagePath;

    public RandomArrayOrderTests(String testName,
                                 String actualPath,
                                 String patternPath,
                                 String expectedMessagePath) {
        this.actualPath = actualPath;
        this.patternPath = patternPath;
        this.expectedMessagePath = expectedMessagePath;
    }

    @Test
    public void test() throws Throwable {
        String actual = readFile(this.actualPath);
        String pattern = readFile(this.patternPath);

        ThrowingRunnable testInvocation = () -> Majx.assertJsonMatchesAnyArrayOrder(pattern, actual);

        if (this.expectedMessagePath != null) {
            String expectedMessage = readFile(this.expectedMessagePath);
            assertThrows(expectedMessage, AssertionError.class, testInvocation);
        } else {
            testInvocation.run();
        }
    }
}
