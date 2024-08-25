package com.jvm_bloggers.validation

import com.networknt.schema.ValidationMessage
import com.networknt.schema.ValidatorTypeCode.*
import java.util.*

object ValidatorMessageTranslator {

    fun translate(messages: Set<ValidationMessage>): String =
        messages.joinToString("\n") { createMessage(it) }

    private fun createMessage(message: ValidationMessage): String =
        when (message.type) {
            REQUIRED.value -> "Element '${message.path}' does not contain required elements '${message.arguments}'!"
            PATTERN.value -> "Element '${message.path}' does not match the required pattern '${message.arguments}'!"
            MIN_LENGTH.value, MAX_LENGTH.value -> "Element '${message.path}' has an invalid '${message.type}' length - the valid one is '${message.arguments}'!"
            UNIQUE_ITEMS.value -> "Array '${message.path}' contains not unique elements!"
            else -> defaultViolation(message)
        }

    private fun defaultViolation(message: ValidationMessage): String =
        Optional.of(message)
            .map { it.message }
            .filter { it.isNotBlank() }
            .filter { it.contains(":") }
            .map { it.substring(it.indexOf(':') + 2) }
            .map { "Element '$message.path' violates '$message.type' rule - '$it'" }
            .map { it.toString() }
            .orElse(message.message)
}