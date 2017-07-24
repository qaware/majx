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
public class WildcardTests {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        "empty object matches any object pattern",
                        "wildcard/anyContent/actualEmpty/object/actual.json",
                        "wildcard/anyContent/actualEmpty/object/pattern.json",
                        null
                },
                {
                        "empty array matches any array pattern",
                        "wildcard/anyContent/actualEmpty/array/actual.json",
                        "wildcard/anyContent/actualEmpty/array/pattern.json",
                        null
                },
                {
                        "single property object matches any object pattern",
                        "wildcard/anyContent/actualSingleEntry/object/actual.json",
                        "wildcard/anyContent/actualSingleEntry/object/pattern.json",
                        null
                },
                {
                        "single element array matches any array pattern",
                        "wildcard/anyContent/actualSingleEntry/array/actual.json",
                        "wildcard/anyContent/actualSingleEntry/array/pattern.json",
                        null
                },
                {
                        "multiple property object matches any object pattern",
                        "wildcard/anyContent/actualMultipleEntries/object/actual.json",
                        "wildcard/anyContent/actualMultipleEntries/object/pattern.json",
                        null
                },
                {
                        "multiple elements array matches any array pattern",
                        "wildcard/anyContent/actualMultipleEntries/array/actual.json",
                        "wildcard/anyContent/actualMultipleEntries/array/pattern.json",
                        null
                },
                {
                        "array wildcard: error invalid start",
                        "wildcard/arrayWildcard/errorInvalidStart/actual.json",
                        "wildcard/arrayWildcard/errorInvalidStart/pattern.json",
                        "wildcard/arrayWildcard/errorInvalidStart/expectedMessage.txt"
                },
                {
                        "array wildcard: actual array too short",
                        "wildcard/arrayWildcard/errorActualArrayTooShort/actual.json",
                        "wildcard/arrayWildcard/errorActualArrayTooShort/pattern.json",
                        "wildcard/arrayWildcard/errorActualArrayTooShort/expectedMessage.txt",
                },
                {
                        "array wildcard: success with no additional elements",
                        "wildcard/arrayWildcard/successNoAdditionalElements/actual.json",
                        "wildcard/arrayWildcard/successNoAdditionalElements/pattern.json",
                        null
                },
                {
                        "array wildcard: success with multiple additional elements",
                        "wildcard/arrayWildcard/successMultipleAdditionalElements/actual.json",
                        "wildcard/arrayWildcard/successMultipleAdditionalElements/pattern.json",
                        null
                },
                {
                        "object wildcard: actual object has too few properties",
                        "wildcard/objectWildcard/errorActualArrayTooShort/actual.json",
                        "wildcard/objectWildcard/errorActualArrayTooShort/pattern.json",
                        "wildcard/objectWildcard/errorActualArrayTooShort/expectedMessage.txt",
                },
                {
                        "object wildcard: success with no additional properties",
                        "wildcard/objectWildcard/successNoAdditionalElements/actual.json",
                        "wildcard/objectWildcard/successNoAdditionalElements/pattern.json",
                        null
                },
                {
                        "object wildcard: success with multiple additional properties",
                        "wildcard/objectWildcard/successMultipleAdditionalElements/actual.json",
                        "wildcard/objectWildcard/successMultipleAdditionalElements/pattern.json",
                        null
                },
                {
                        "value wildcard: success with null",
                        "wildcard/valueWildcard/successNull/actual.json",
                        "wildcard/valueWildcard/successNull/pattern.json",
                        null
                },
                {
                        "value wildcard: success with number",
                        "wildcard/valueWildcard/successNumber/actual.json",
                        "wildcard/valueWildcard/successNumber/pattern.json",
                        null
                },
                {
                        "value wildcard: success with boolean",
                        "wildcard/valueWildcard/successBoolean/actual.json",
                        "wildcard/valueWildcard/successBoolean/pattern.json",
                        null
                },
                {
                        "value wildcard: success with string",
                        "wildcard/valueWildcard/successString/actual.json",
                        "wildcard/valueWildcard/successString/pattern.json",
                        null
                },
                {
                        "value wildcard: success with array",
                        "wildcard/valueWildcard/successArray/actual.json",
                        "wildcard/valueWildcard/successArray/pattern.json",
                        null
                },
                {
                        "value wildcard: success with object",
                        "wildcard/valueWildcard/successObject/actual.json",
                        "wildcard/valueWildcard/successObject/pattern.json",
                        null
                }
        });
    }

    private String actualPath;
    private String patternPath;
    private String expectedMessagePath;

    public WildcardTests(String testName,
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

        if (this.expectedMessagePath != null) {
            String expectedMessage = readFile(this.expectedMessagePath);

            exception.expect(AssertionError.class);
            exception.expectMessage(expectedMessage);
        }

        Majx.assertJsonMatches(pattern, actual);
    }
}
