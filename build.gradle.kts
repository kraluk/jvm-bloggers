import com.jvm_bloggers.validation.JsonValidationTask
import java.nio.file.Paths
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:2.7.18")
        classpath("com.bmuschko:gradle-docker-plugin:9.4.0")
        classpath("com.github.ben-manes:gradle-versions-plugin:0.51.0")
    }
}

plugins {
    java
    application
    groovy
    idea
    eclipse
    jacoco
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.1.6"
    id("io.freefair.lombok") version "8.6"
    id("com.bmuschko.docker-spring-boot-application") version "9.4.0"
    id("com.github.ben-manes.versions") version "0.51.0"
}

group = "com.jvm-bloggers"
version = "3.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

object Versions {
    const val WICKET = "9.18.0"
    const val JERSEY = "2.43"
}

dependencies {
    // Spring Boot stuff
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.github.ulisesbocchio:jasypt-spring-boot:3.0.5")
    implementation("org.springframework.boot:spring-boot-devtools")

    // Logic
    implementation("com.rometools:rome:2.1.0")

    // Facebook
    implementation("com.restfb:restfb:3.24.0")

    // Twitter
    implementation("org.twitter4j:twitter4j-core:4.0.7")

    // View
    implementation("com.giffing.wicket.spring.boot.starter:wicket-spring-boot-starter:3.1.7")
    implementation("org.apache.wicket:wicket-core:${Versions.WICKET}")
    implementation("org.apache.wicket:wicket-spring:${Versions.WICKET}")
    implementation("org.apache.wicket:wicket-ioc:${Versions.WICKET}")
    implementation("org.apache.wicket:wicket-devutils:${Versions.WICKET}")
    implementation("org.apache.wicket:wicket-auth-roles:${Versions.WICKET}")
    implementation("org.apache.wicket:wicket-bean-validation:${Versions.WICKET}")
    implementation("org.wicketstuff:wicketstuff-annotation:${Versions.WICKET}")
    implementation("com.googlecode.wicket-jquery-ui:wicket-jquery-ui:9.18.0")
    implementation("com.googlecode.wicket-jquery-ui:wicket-jquery-ui-plugins:9.18.0")

    implementation("de.agilecoders.wicket.webjars:wicket-webjars:3.0.7")
    implementation("org.webjars:jquery:3.6.3")
    implementation("org.webjars:webjars-locator:0.52")

    implementation("org.webjars:startbootstrap-sb-admin-2:3.3.7+1")
    implementation("org.webjars:html5shiv:3.7.3-1")
    implementation("org.webjars:respond:1.4.2-1")
    implementation("org.webjars.npm:ev-emitter:1.1.1")
    implementation("org.webjars.npm:fizzy-ui-utils:2.0.7")
    implementation("org.webjars.npm:infinite-scroll:3.0.6")
    implementation("org.webjars:toastr:2.1.2")


    // Utils and helpers
    implementation("org.projectlombok:lombok:1.18.34")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("org.apache.commons:commons-text:1.12.0")
    implementation("org.antlr:ST4:4.3.4")
    implementation("org.glassfish.jersey.core:jersey-client:${Versions.JERSEY}")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:${Versions.JERSEY}")
    implementation("org.glassfish.jersey.inject:jersey-hk2:${Versions.JERSEY}")
    implementation("net.jcip:jcip-annotations:1.0")
    implementation("org.objenesis:objenesis:3.4")
    implementation("commons-validator:commons-validator:1.9.0")
    implementation("io.vavr:vavr:0.10.4")
    implementation("org.jsoup:jsoup:1.18.1")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.json:json:20240303")
    implementation("com.vdurmont:emoji-java:5.1.1") {
        exclude(group = "org.json", module = "json")
    }

    // Database related
    implementation("org.postgresql:postgresql")
    implementation("org.liquibase:liquibase-core")
    implementation("com.mattbertolini:liquibase-slf4j:4.1.0")
    implementation("org.hibernate:hibernate-search-orm:5.11.11.Final")
    implementation("org.hibernate:hibernate-search-engine:5.11.11.Final")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "com.vaadin.external.google", module = "android-json")
    }
    testImplementation("org.spockframework:spock-core:2.3-groovy-4.0")
    testImplementation("org.spockframework:spock-spring:2.3-groovy-4.0")
    testImplementation("org.springframework:spring-test")
    testImplementation("com.jayway.jsonpath:json-path:2.9.0")
    testImplementation("org.apache.groovy:groovy:4.0.22")
    testImplementation("org.apache.groovy:groovy-json:4.0.22")
    testRuntimeOnly("com.h2database:h2")
}

docker {
    springBootApplication {
        baseImage = "eclipse-temurin:21.0.3_9-jre-alpine"
        maintainer = "Tomasz Dziurko \"tdziurko at gmail dottt com\""
        ports = listOf(8080, 8080)
        images = setOf(
            "jvmbloggers/jvm-bloggers:" + project.version + "-" + getTimestampWithGitHash(),
            "jvmbloggers/jvm-bloggers:latest"
        )
        jvmArgs = listOf("--add-opens", "java.base/java.lang=ALL-UNNAMED", "-Dwicket.ioc.useByteBuddy=true")
    }

    registryCredentials {
        username = getConfigurationProperty("DOCKER_USERNAME", "docker.username")
        password = getConfigurationProperty("DOCKER_PASSWORD", "docker.password")
        email = getConfigurationProperty("DOCKER_EMAIL", "docker.email")
    }
}

tasks.dockerCreateDockerfile {
    instruction("RUN apk update && apk add ca-certificates && update-ca-certificates && apk add openssl")
}

tasks.bootRun {
    @Suppress("UNCHECKED_CAST")
    systemProperties = System.getProperties() as Map<String, Any>
    jvmArgs = listOf("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}

tasks.test {
    systemProperty("file.encoding", "utf-8")
    dependsOn("validateBlogsData")
    useJUnitPlatform()
    jvmArgs = listOf("--add-opens", "java.base/jdk.internal.loader=ALL-UNNAMED", "--add-opens", "java.base/java.lang=ALL-UNNAMED")
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

tasks.dependencyUpdates {
    timeout.set(Duration.ofMillis(10000))
}

tasks.register("stage") {
    dependsOn("build", "clean")
    mustRunAfter("clean")
}

tasks.register("validateBlogsData") {
    val blogsDir = Paths.get("$projectDir", "src", "main", "resources", "blogs")
    val schema = blogsDir.resolve("schema.json")

    val blogsData = listOf("bloggers", "presentations", "companies", "podcasts")

    blogsData.forEach { fileName ->
        val taskName = "validate" + fileName.replaceFirstChar { it.uppercase() }
        val json = blogsDir.resolve("${fileName}.json")

        tasks.register(taskName, JsonValidationTask::class.java, schema, json)
        dependsOn(taskName) // dependsOn?
    }
}

fun getConfigurationProperty(envVar: String, sysProp: String): String =
    System.getenv(envVar) ?: project.findProperty(sysProp).toString()


fun getTimestampWithGitHash(): String {
    val timeStamp: String = DateTimeFormatter
        .ofPattern("yyyyMMdd-HHmmss")
        .withZone(ZoneOffset.UTC)
        .format(Instant.now())

    val cmd = "git log --pretty=format:%h -n 1"
    val proc = Runtime.getRuntime().exec(cmd)

    proc.waitFor()
    return "$timeStamp-${proc.inputStream.bufferedReader().readText().trim()}"
}