package com.jvm_bloggers.validation

import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaException
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import java.nio.file.Path
import kotlin.io.path.inputStream

class JsonValidator(
    private val schemaPath: Path,
    private val jsonPath: Path

) {
    fun validate() {
        try {
            schemaPath.inputStream().use { schemaStream ->
                jsonPath.inputStream().use { jsonStream ->
                    val schema = factory.getSchema(schemaStream)
                    val json = mapper.readTree(jsonStream)

                    validateData(schema, json)
                }
            }
        } catch (e: Exception) {
            when (e) {
                is JacksonException,
                is JsonSchemaException -> {
                    throw JsonValidationException(
                        """Unable to process the JSON validation - probably the validated file and/or schema 
                            file have an invalid structure or format!
                        """.trimMargin().replace("\n", ""),
                        e
                    )
                }

                else -> throw e
            }
        }
    }

    private fun validateData(schema: JsonSchema, json: JsonNode) {
        val errors = schema.validate(json)

        if (errors.isNotEmpty()) {
            val violations = ValidatorMessageTranslator.translate(errors)
            throw JsonValidationException("Unable to validate '$json' due to: \n${violations}")
        }
    }

    companion object {
        private val mapper = ObjectMapper()
        private val factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4)
    }
}

class JsonValidationException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}