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
