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
public class MissingTests {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        "single property missing in object",
                        "missing/singleMissingProperty/object/actual.json",
                        "missing/singleMissingProperty/object/pattern.json",
                        "missing/singleMissingProperty/object/expectedMessage.txt"
                },
                {
                        "single element missing in array",
                        "missing/singleMissingProperty/array/actual.json",
                        "missing/singleMissingProperty/array/pattern.json",
                        "missing/singleMissingProperty/array/expectedMessage.txt"
                },
                {
                        "multiple properties missing in object",
                        "missing/multipleMissingProperties/object/actual.json",
                        "missing/multipleMissingProperties/object/pattern.json",
                        "missing/multipleMissingProperties/object/expectedMessage.txt"
                },
                {
                        "multiple properties missing in array",
                        "missing/multipleMissingProperties/array/actual.json",
                        "missing/multipleMissingProperties/array/pattern.json",
                        "missing/multipleMissingProperties/array/expectedMessage.txt"
                }
        });
    }

    private String actualPath;
    private String patternPath;
    private String expectedMessagePath;

    public MissingTests(String testName,
                        String actualPath,
                        String patternPath,
                        String expectedMessagePath) {
        this.actualPath = actualPath;
        this.patternPath = patternPath;
        this.expectedMessagePath = expectedMessagePath;
    }

    @Test
    public void test() throws IOException {
        String actual = readFile(this.actualPath);
        String pattern = readFile(this.patternPath);
        String expectedMessage = readFile(this.expectedMessagePath);

        exception.expect(AssertionError.class);
        exception.expectMessage(expectedMessage);
        Majx.assertJsonMatches(pattern, actual);
    }
}
