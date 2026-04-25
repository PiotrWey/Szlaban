import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import xyz.jpenilla.runpaper.task.RunServer

plugins {
  java
  jacoco
  id("xyz.jpenilla.run-paper") version "3.0.2"
  id("com.gradleup.shadow") version "9.4.1"
  id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
  id("de.eldoria.plugin-yml.bukkit") version "0.9.0"
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

  // implementation apis
  implementation("org.bstats:bstats-bukkit:3.2.1")

  // unit tests
  testImplementation(platform("org.junit:junit-bom:6.0.3"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  // mocking framework
  testImplementation("org.mockito:mockito-core:5.+")

  // bukkit
  bukkitLibrary("com.google.code.gson", "gson", "2.10.1")
}

tasks {
  // run a server with the recommended dependencies
  named<RunServer>("runServer") {
    minecraftVersion(targetMcVersion)
    // disable bstats to avoid counting development instances
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
    // Relocate bStats into the plugin's package to avoid conflicts with other
    // plugins using bStats
    relocate("org.bstats", project.group.toString())
  }

  test {
    finalizedBy(named<JacocoReport>("jacocoTestReport"))
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading" )
  }

  named<JacocoReport>("jacocoTestReport") {
    dependsOn(test)
    reports {
      xml.required = true
      html.required = false
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

bukkit {
  main = project.property("main") as String
  load = BukkitPluginDescription.PluginLoadOrder.STARTUP;
  apiVersion = project.property("target") as String
  prefix = project.property("prefix") as String
  foliaSupported = false

  description = project.property("description") as String
  website = project.property("website") as String
  authors = (project.property("authors") as String).trim('[', ']').split(",").map { it.trim() }
  contributors = (project.property("contributors") as String).trim('[', ']').split(",").map { it.trim() }.filter { it.isNotEmpty() }

  depend = (project.property("depend") as String).trim('[', ']').split(",").map { it.trim() }.filter { it.isNotEmpty() }
  softDepend = (project.property("softDepend") as String).trim('[', ']').split(",").map { it.trim() }.filter { it.isNotEmpty() }

  permissions {
    register("szlaban.*") {
      description = "Grants all Szlaban permissions"
      default = BukkitPluginDescription.Permission.Default.OP
      children = listOf("szlaban.admin", "szlaban.use")
    }
    register("szlaban.admin") {
      description = "Grants admin permissions"
      default = BukkitPluginDescription.Permission.Default.OP
    }
    register("szlaban.advisor") {
      description = "Allows full use of the Advisor module"
      default = BukkitPluginDescription.Permission.Default.OP
    }
  }
}
