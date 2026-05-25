plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

tasks.withType<JavaCompile>().configureEach {
    doFirst {
        options.compilerArgs.removeAll { it.contains("jabel", ignoreCase = true) }
        options.annotationProcessorPath = files()
        classpath = classpath.filter {
            !it.name.contains("jabel", ignoreCase = true) && !it.name.contains("byte-buddy", ignoreCase = true)
        }
    }
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("LOTR-PickupFilter-Addon")
}

extra["modVersion"] = "dev-local"
