subprojects {
    group = "city.soupbowl"
    version = "1.0.0"

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
}