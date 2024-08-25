package com.jvm_bloggers.validation

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.nio.file.Path
import javax.inject.Inject

class JsonValidationTask(
    @Inject private val schemaFile: Path,
    @Inject private val jsonFile: Path
) : DefaultTask() {

    private val validator: JsonValidator = JsonValidator(schemaFile, jsonFile)

    @TaskAction
    fun validate() {
        validator.validate()
    }
}