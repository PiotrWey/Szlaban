import xyz.jpenilla.runpaper.task.RunServer

plugins {
  java
  id("xyz.jpenilla.run-paper") version "3.0.2"
  id("com.gradleup.shadow") version "9.4.1"
  id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
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
  // Paper dev bundle
  paperweight.paperDevBundle("${targetMcVersion}-R0.1-SNAPSHOT")

  // compile-against APIs
  // compileOnly("io.papermc.paper:paper-api:${targetMcVersion}-R0.1-SNAPSHOT")
  compileOnly("net.dmulloy2:ProtocolLib:5.4.0")

  // unit tests
  testImplementation(platform("org.junit:junit-bom:6.0.3"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  // mocking framework
  testImplementation("org.mockito:mockito-core:5.+")
  // libraries for tests
  // testImplementation("io.papermc.paper:paper-api:${targetMcVersion}-R0.1-SNAPSHOT")
}

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

  shadowJar {
    manifest {
      attributes(
        "paperweight-mappings-namespace" to "mojang"
      )
    }
  }

  test {
    useJUnitPlatform()
    jvmArgs(
      "-XX:+EnableDynamicAgentLoading"
    )
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
