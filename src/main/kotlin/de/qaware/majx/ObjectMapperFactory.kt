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

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import java.text.SimpleDateFormat
import java.util.*

/**
 * @return New object mapper with standard settings
 */
fun createObjectMapper(): ObjectMapper {
    val mapper = ObjectMapper()

    // Configure POJO to JSON features
    configureSerializationFeatures(mapper)

    // Configure JSON to POJO features
    configureDeserializationFeatures(mapper)

    //set up ISO 8601 date/time stamp format:
    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'", Locale.ENGLISH)
    df.timeZone = TimeZone.getTimeZone("UTC")
    mapper.dateFormat = df

    return mapper
}

private fun configureDeserializationFeatures(mapper: ObjectMapper) {
    // Add Joda module so Jackson deserializes dates correctly
    mapper.registerModule(JodaModule())

    // Ignore unknown properties in input data. This creates forward compatibility in case a backend adds new
    // attributes. Similar to JAXB deserialization which also ignores unknown properties by default.
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    // Allow (non-standard) JSON comments in input data
    mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true)
}

private fun configureSerializationFeatures(mapper: ObjectMapper) {
    // Pretty print
    mapper.configure(SerializationFeature.INDENT_OUTPUT, true)

    // Output empty objects
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)

    // Output dates as strings, not as arrays (AS_TIMESTAMP means actually as array)
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    // Do not output null elements.
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
}
