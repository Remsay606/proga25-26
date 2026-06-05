plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(project(":common"))
    implementation("com.opencsv:opencsv:5.9")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest {
        attributes["Main-Class"] = "ru.codehub.server.ServerMain"
    }
    archiveClassifier.set("")
    archiveBaseName.set("server")
}
