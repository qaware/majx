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

import static de.qaware.majx.TestSupportKt.readFile;

/**
 * Tests that the examples in README and wiki actually work.
 */
public class DocsTests {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    // Exact match

    @Test
    public void exactMatchSuccess() throws Exception {
        String actual = readFile("docs/exact/actual.json");
        String pattern = readFile("docs/exact/pattern.json");
        Majx.assertJsonMatches(pattern, actual);
    }

    @Test
    public void exactMatchDifferentOrderSuccess() throws Exception {
        String actual = readFile("docs/exact/actual.differnt-order.json");
        String pattern = readFile("docs/exact/pattern.json");
        Majx.assertJsonMatches(pattern, actual);
    }

    @Test
    public void exactMatchError() throws Exception {
        String actual = readFile("docs/exact/actual.error.json");
        String pattern = readFile("docs/exact/pattern.json");
        exception.expect(AssertionError.class);
        Majx.assertJsonMatches(pattern, actual);
    }

    // Array

    @Test
    public void arrayWildcardSuccess() throws Exception {
        String actual = readFile("docs/wildcard/array/actual.json");
        String pattern = readFile("docs/wildcard/array/pattern.with-wildcard.json");
        Majx.assertJsonMatches(pattern, actual);
    }

    @Test
    public void arrayWildcardAnyEntriesSuccess() throws Exception {
        String actual = readFile("docs/wildcard/array/actual.json");
        String pattern = readFile("docs/wildcard/array/pattern.any-entries.json");
        Majx.assertJsonMatches(pattern, actual);
    }

    @Test
    public void arrayWildcardInvalidStart() throws Exception {
        String actual = readFile("docs/wildcard/array/actual.invalid-start.json");
        String pattern = readFile("docs/wildcard/array/pattern.with-wildcard.json");
        exception.expect(AssertionError.class);
        Majx.assertJsonMatches(pattern, actual);
    }


    @Test
    public void arrayWildcardExact() throws Exception {
        String actual = readFile("docs/wildcard/array/actual.json");
        String pattern = readFile("docs/wildcard/array/pattern.exact.json");
        exception.expect(AssertionError.class);
        Majx.assertJsonMatches(pattern, actual);
    }

    // Object

    @Test
    public void objectWildcardSuccess() throws Exception {
        String actual = readFile("docs/wildcard/object/actual.json");
        String pattern = readFile("docs/wildcard/object/pattern.with-wildcard.json");
        Majx.assertJsonMatches(pattern, actual);
    }

    @Test
    public void objectWildcardAnyEntriesSuccess() throws Exception {
        String actual = readFile("docs/wildcard/object/actual.json");
        String pattern = readFile("docs/wildcard/object/pattern.any-entries.json");
        Majx.assertJsonMatches(pattern, actual);
    }

    @Test
    public void objectWildcardExact() throws Exception {
        String actual = readFile("docs/wildcard/object/actual.json");
        String pattern = readFile("docs/wildcard/object/pattern.exact.json");
        exception.expect(AssertionError.class);
        Majx.assertJsonMatches(pattern, actual);
    }

    // Ignoring values

    @Test
    public void ignoringValuesSuccess() throws Exception {
        String actual = readFile("docs/ignoringValues/actual.json");
        String pattern = readFile("docs/ignoringValues/pattern.json");
        Majx.assertJsonMatches(pattern, actual);
    }

    @Test
    public void ignoringValuesObjectValueSuccess() throws Exception {
        String actual = readFile("docs/ignoringValues/actual.object-value.json");
        String pattern = readFile("docs/ignoringValues/pattern.json");
        Majx.assertJsonMatches(pattern, actual);
    }

    @Test
    public void ignoringValuesNullValueSuccess() throws Exception {
        String actual = readFile("docs/ignoringValues/actual.null-value.json");
        String pattern = readFile("docs/ignoringValues/pattern.json");
        Majx.assertJsonMatches(pattern, actual);
    }
}
