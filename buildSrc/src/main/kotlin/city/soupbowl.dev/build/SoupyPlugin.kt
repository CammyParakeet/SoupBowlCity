package city.soupbowl.dev.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories

class SoupyPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val ext = if (extensions.findByName("soupyConfig") != null) {
                extensions.getByName("soupyConfig") as SoupyExtension
            } else {
                extensions.create("soupyConfig", SoupyExtension::class.java)
            }

            val catalog = target.extensions.getByType<VersionCatalogsExtension>().named("soupy")

            plugins.apply("java")
            plugins.apply("java-library")

            extensions.findByType(JavaPluginExtension::class.java)?.apply {
                toolchain.languageVersion.set(JavaLanguageVersion.of(21))
            }

            // Apply Kotlin plugin only if kotlin-stdlib is in the version catalog
//            catalog.findPlugin("kotlin-jvm").ifPresent { pluginDef ->
//                plugins.apply(pluginDef.get().pluginId)
//
//                dependencies.add(
//                    "implementation",
//                    "org.jetbrains.kotlin:kotlin-stdlib:${catalog.findVersion("kotlin").get().requiredVersion}"
//                )
//
//                val javaExt = extensions.getByType<JavaPluginExtension>()
//                javaExt.sourceSets.getByName("main").java.srcDirs("src/main/java", "src/main/kotlin")
//
//                catalog.findPlugin("kotlin-kapt").ifPresent { kaptDef ->
//                    plugins.apply(kaptDef.get().pluginId)
//                }
//
//                // Add AutoService via kapt
//                dependencies.add("compileOnly", catalog.findLibrary("auto-service-annotations").get())
//                dependencies.add("kapt", catalog.findLibrary("auto-service").get())
//            }

            afterEvaluate {
                if (ext.usePaper && ext.usePaperweight) {
                    throw IllegalStateException("You cannot enable both usePaper and usePaperweight. Please choose one.")
                }

                // Paperweight setup
                if (ext.usePaperweight) {
                    val plugin = catalog.findPlugin("paperweight-userdev").get()
                    plugins.apply(plugin.get().pluginId)

                    val version = catalog.findVersion("paper-api").get().requiredVersion
                    dependencies.add("paperweightDevelopmentBundle", "io.papermc.paper:paper-dev-bundle:$version")
                }

                // Paper API (classic)
                if (ext.usePaper) {
                    val version = catalog.findVersion("paper-api").get().requiredVersion
                    dependencies.add("compileOnly", "io.papermc.paper:paper-api:$version")
                }

                // Lombok
                if (ext.useLombok) {
                    val plugin = catalog.findPlugin("lombok").get()
                    plugins.apply(plugin.get().pluginId)

                    val version = catalog.findVersion("lombok").get().requiredVersion
                    dependencies.add("compileOnly", "org.projectlombok:lombok:$version")
                    dependencies.add("annotationProcessor", "org.projectlombok:lombok:$version")
                }

                // Shadow
                if (ext.useShadow) {
                    val plugin = catalog.findPlugin("shadow").get()
                    plugins.apply(plugin.get().pluginId)
                }
            }

            repositories {
                mavenCentral()
                maven("https://repo.papermc.io/repository/maven-public/")
                maven("https://maven.pkg.github.com/Aviara-CC/AviaraLibs") {
                    credentials {
                        username = project.findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")
                        password = project.findProperty("gpr.key") as String? ?: System.getenv("GPR_TOKEN")
                    }
                }
            }

            // Default AviaraLibs compileOnly
            dependencies {
                add("compileOnly", catalog.findLibrary("aviara-libs").get())
                add("annotationProcessor", catalog.findLibrary("auto-service").get())
                add("compileOnly", catalog.findLibrary("auto-service-annotations").get())
            }
        }
    }
}