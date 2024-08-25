buildscript {

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
    groovy
    idea
    eclipse
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.networknt:json-schema-validator:1.0.81")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")

    testImplementation("org.spockframework:spock-core:2.3-groovy-3.0")
    testImplementation("org.codehaus.groovy:groovy:3.0.21")
    testImplementation("org.codehaus.groovy:groovy-json:3.0.21")
}

tasks.test {
    systemProperty("file.encoding", "utf-8")
    useJUnitPlatform()
}
