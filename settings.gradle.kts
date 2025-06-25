rootProject.name = "SoupbowlCityPlugins"

dependencyResolutionManagement {
    versionCatalogs {
        create("soupy") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
    plugins {
        id("io.freefair.lombok") version "8.6"
        id("com.github.johnrengelman.shadow") version "8.1.1"
        id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
        id("org.jetbrains.kotlin.jvm") version "1.9.22"
    }
}

include("SoupyCore")


