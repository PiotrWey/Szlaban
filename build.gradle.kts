@file:Suppress("UNRESOLVED_REFERENCE")
// IntelliJ has a problem where it screams at me when I try to use the
// ShadowJar plugin in Gradle Kotlin DSL. This gets rid of 2/3 of associated
// issues.

import xyz.jpenilla.runpaper.task.RunServer
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "9.4.1"
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
    // compile-against APIs
    compileOnly("io.papermc.paper:paper-api:${targetMcVersion}-R0.1-SNAPSHOT")
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")

    // unit tests
    testImplementation(platform("org.junit:junit-bom:6.0.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // libraries for tests
    testImplementation("io.papermc.paper:paper-api:${targetMcVersion}-R0.1-SNAPSHOT")


    tasks {
        // run a server with the recommended dependencies
        named<RunServer>("runServer") {
            minecraftVersion(targetMcVersion)
            downloadPlugins {
                github("dmulloy2", "ProtocolLib", "5.4.0", "ProtocolLib.jar")
            }
        }

        jar {
            manifest {
                attributes(
                    "paperweight-mappings-namespace" to "mojang"
                )
            }
        }

        // Non-standard syntax. Due to bugs with how IntelliJ loads the ShadowJar plugin
        // This should be replaced with the following line if it is fixed
        // shadowJar {
        named<ShadowJar>("shadowJar", ShadowJar::class) {
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
