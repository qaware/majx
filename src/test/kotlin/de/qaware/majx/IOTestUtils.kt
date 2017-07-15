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
package de.qaware.majx

import org.apache.commons.io.IOUtils

import java.io.IOException
import java.io.InputStream

/**
 * Read file from resources as string using UTF 8 encoding.
 *
 * @param fileName The file name.
 * @return The file content as string.
 * @throws IOException If file could not be read.
 */
@Throws(IOException::class)
fun readFile(fileName: String): String = openResource(fileName).use { stream ->
    return IOUtils.toString(stream, "UTF-8")
}

/**
 * Open stream to file in resources.

 * @param fileName The file name.
 * @return The stream. Has to be closed by caller.
 * @throws IOException If file could not be read.
 */
@Throws(IOException::class)
private fun openResource(fileName: String): InputStream =
        Thread.currentThread().contextClassLoader.getResourceAsStream(fileName) ?:
                throw IllegalArgumentException(String.format("Resource %s not found", fileName))
