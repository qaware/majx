/**
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
public class MiscTests {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        "mismatch in root type",
                        "misc/mismatchInRootType/actual.json",
                        "misc/mismatchInRootType/pattern.json",
                        AssertionError.class,
                        "misc/mismatchInRootType/expectedMessage.txt"
                },
                {
                        "invalid json: in actual",
                        "misc/invalidJson/inActual/actual.json",
                        "misc/invalidJson/inActual/pattern.json",
                        IllegalArgumentException.class,
                        "misc/invalidJson/inActual/expectedMessage.txt"
                },
                {
                        "invalid json: in pattern",
                        "misc/invalidJson/inPattern/actual.json",
                        "misc/invalidJson/inPattern/pattern.json",
                        IllegalArgumentException.class,
                        "misc/invalidJson/inPattern/expectedMessage.txt"
                }
        });
    }

    private String actualPath;
    private String patternPath;
    private Class<? extends Throwable> expectedExceptionType;
    private String expectedMessagePath;

    public MiscTests(String testName,
                     String actualPath,
                     String patternPath,
                     Class<? extends Throwable> expectedExceptionType,
                     String expectedMessagePath) {
        this.actualPath = actualPath;
        this.patternPath = patternPath;
        this.expectedExceptionType = expectedExceptionType;
        this.expectedMessagePath = expectedMessagePath;
    }

    @Test
    public void test() throws IOException {
        String actual = readFile(this.actualPath);
        String pattern = readFile(this.patternPath);
        String expectedMessage = readFile(this.expectedMessagePath);

        exception.expect(this.expectedExceptionType);
        exception.expectMessage(expectedMessage);
        MajxKt.assertJsonMatches(actual, pattern);
    }
}
