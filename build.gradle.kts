@file:Suppress("UNRESOLVED_REFERENCE")
// IntelliJ has a problem where it screams at me when I try to use the
// ShadowJar plugin in Gradle Kotlin DSL. This gets rid of 2/3 of associated
// issues.

import xyz.jpenilla.runpaper.task.RunServer
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "9.4.0"
}

group = project.property("group") as String
version = project.property("version") as String


val targetMcVersion = project.property("target") as String

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    // Paper API
    compileOnly("io.papermc.paper:paper-api:${targetMcVersion}-R0.1-SNAPSHOT")
    // Protocollib
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")

    // unit tests
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.papermc.paper:paper-api:${targetMcVersion}-R0.1-SNAPSHOT")

    tasks {
        named<RunServer>("runServer") {
            minecraftVersion(targetMcVersion)
        }

        jar {
            manifest {
                attributes(
                    "paperweight-mappings-namespace" to "mojang"
                )
            }
        }

        // all errors in this function are fake, it's just an attention seeker
        shadowJar {
            manifest {
                attributes(
                    "paperweight-mappings-namespace" to "mojang"
                )
            }
        }

        test {
            useJUnitPlatform()
        }
    }
}

val targetJavaVersion = 21

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion

    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    // dynamic for the plugin.json
    val props = mapOf(
        "version" to version,
        "name" to project.property("name") as String,
        "description" to project.property("description") as String,
        "website" to project.property("website") as String,
        "authors" to (project.property("authors") as String).trim('[', ']').split(","),
        "contributors" to (project.property("contributors") as String).trim('[', ']').split(","),
        "main" to project.property("main") as String,
        "prefix" to project.property("prefix") as String,
        "api_version" to targetMcVersion,
    )
    inputs.properties(props)
    filteringCharset = "UTF-8"

    filesMatching("plugin.yml") {
        expand(props)
    }
}
