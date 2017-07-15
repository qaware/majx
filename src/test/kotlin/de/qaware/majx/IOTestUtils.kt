package de.qaware.majx

import org.apache.commons.io.IOUtils

import java.io.IOException
import java.io.InputStream

/**
 * Read file from resources as string using UTF 8 encoding.
 *
 * @param fileName File name
 * @return File content
 * @throws IOException If file could not be read
 */
@Throws(IOException::class)
fun readFile(fileName: String): String {
    openResource(fileName).use { stream -> return IOUtils.toString(stream, "UTF-8") }
}

/**
 * Open stream to file in resources

 * @param fileName File name
 * @return Stream. Has to be closed by caller
 * @throws IOException If file could not be read
 */
@Throws(IOException::class)
private fun openResource(fileName: String): InputStream {
    val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(fileName) ?:
            throw IllegalArgumentException(String.format("Resource %s not found", fileName))
    return stream
}
