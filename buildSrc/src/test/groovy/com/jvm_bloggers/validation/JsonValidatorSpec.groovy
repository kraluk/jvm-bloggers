package com.jvm_bloggers.validation

import com.fasterxml.jackson.core.JsonParseException
import com.networknt.schema.JsonSchemaException
import spock.lang.Specification
import spock.lang.Subject

import java.nio.charset.StandardCharsets
import java.nio.file.Path

@Subject(JsonValidator)
class JsonValidatorSpec extends Specification {

    def "Should validate successfully given JSON file against given Schema"() {
        given:
        def schemaSupplier = tempPathOf(Jsons.SCHEMA)
        def jsonSupplier = tempPathOf(Jsons.VALID)

        def validator = new JsonValidator(schemaSupplier, jsonSupplier)

        when:
        validator.validate()

        then:
        noExceptionThrown()
    }

    def "Should throw an exception when the validation is not ended successfully"() {
        given:
        def schemaSupplier = tempPathOf(Jsons.SCHEMA)
        def jsonSupplier = tempPathOf(Jsons.INVALID)

        def validator = new JsonValidator(schemaSupplier, jsonSupplier)

        when:
        validator.validate()

        then:
        def e = thrown(JsonValidator.JsonValidationException)

        e.message == "Unable to validate '{\"item\":\"too-short\"}' due to: \nElement '\$.item' has an invalid 'minLength' length - the valid one is '[10]'!"
    }

    def "Should throw an exception when the schema file is invalid"() {
        given:
        def schemaSupplier = tempPathOf("INVALID-SCHEMA")
        def jsonSupplier = tempPathOf("{}")

        def validator = new JsonValidator(schemaSupplier, jsonSupplier)

        when:
        validator.validate()

        then:
        def e = thrown(JsonValidator.JsonValidationException)

        e.message == "Unable to process the JSON validation - probably the validated file and/or schema file have an invalid structure or format!"
        e.cause.class == JsonSchemaException.class
    }

    def "Should throw an exception when the input file is invalid"() {
        given:
        def schemaSupplier = tempPathOf("{}")
        def jsonSupplier = tempPathOf("INVALID-INPUT")

        def validator = new JsonValidator(schemaSupplier, jsonSupplier)

        when:
        validator.validate()

        then:
        def e = thrown(JsonValidator.JsonValidationException)

        e.message == "Unable to process the JSON validation - probably the validated file and/or schema file have an invalid structure or format!"
        e.cause.class == JsonParseException.class
    }
    
    private static Path tempPathOf(final String content) {
        def tempFile = Files.createTempFile("temp", ".json")
        Files.write(tempFile, content.getBytes(StandardCharsets.UTF_8))
        tempFile
    }

    private static class Jsons {
        static def SCHEMA = """
        {
            "\$schema": "http://json-schema.org/draft-04/schema#",
            "id": "https://jvm-bloggers.com/",
            "type": "object",
            "properties": {
                "item": {
                    "type": "string",
                    "minLength": 10,
                    "description": "Item"
                }
            },
            "required": [
                "item"
            ]
        }
        """

        static def VALID = """
        {
            "item": "some-valid-item-name"
        }
        """

        static def INVALID = """
        {
            "item": "too-short"
        }
        """
    }
}
