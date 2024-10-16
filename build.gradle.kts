import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "1.7.1"
  id("xyz.jpenilla.run-paper") version "2.3.0" // Adds runServer and runMojangMappedServer tasks for testing
  id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1" // Generates plugin.yml based on the Gradle config
}

group = "org.esoteric"
version = "0.2.0"
description = "The plugin to manage social aspects of The Slimy Swamp, such as the messaging system, guilds and parties."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.EsotericOrganisation:tss-ranks-plugin:v0.2.2:dev")
    compileOnly("com.github.EsotericOrganisation:tss-core-plugin:v0.2.1:dev-all")

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
    name = "TSSSocial"
    description = project.description
    authors.addAll("Esoteric Organisation", "Esoteric Enderman")

    version = project.version.toString()
    apiVersion = "1.21"
    depend.addAll("TSSCore", "TSSRanks")
    main = "org.esoteric.tss.minecraft.plugins.social.TSSSocialPlugin"
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
}
