import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "1.7.1"
  id("xyz.jpenilla.run-paper") version "2.3.0" // Adds runServer and runMojangMappedServer tasks for testing
  id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1" // Generates plugin.yml based on the Gradle config
}

group = "org.esoteric_organisation"
version = "0.1"
description = "The plugin to manage social aspects of The Slimy Swamp, such as the messaging system, guilds and parties."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.EsotericOrganisation:tss-ranks-plugin:0.1.1:dev")
    compileOnly("com.github.EsotericOrganisation:tss-core-plugin:0.1.6:dev-all")

    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
}

bukkitPluginYaml {
  main = "org.esoteric_organisation.tss_social_plugin.TSSSocialPlugin"
  load = BukkitPluginYaml.PluginLoadOrder.STARTUP
  authors.addAll("Esoteric Organisation", "Esoteric Enderman")
  apiVersion = "1.21"
}
