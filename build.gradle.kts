plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.opencsv:opencsv:5.9")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaExec> {
    args("src/main/resources/collection.csv")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ru.codehub.Main"
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest {
        attributes["Main-Class"] = "ru.codehub.Main"
    }
    archiveClassifier.set("")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.javadoc {
    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).apply {
        charSet = "UTF-8"
        docEncoding = "UTF-8"
        addStringOption("Xdoclint:none", "-quiet")
    }
    classpath += configurations.compileClasspath.get()
    classpath += configurations.runtimeClasspath.get()
}