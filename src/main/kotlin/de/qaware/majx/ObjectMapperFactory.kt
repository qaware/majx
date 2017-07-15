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
