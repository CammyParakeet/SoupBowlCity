plugins {
    `kotlin-dsl`
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    dependencies {
        implementation("io.freefair.gradle:lombok-plugin:8.6")
        implementation("com.github.johnrengelman:shadow:8.1.1")
        implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    }
}