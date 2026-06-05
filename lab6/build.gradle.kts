plugins {
    id("java")
}

allprojects {
    group = "ru.codehub"
    version = "1.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.javadoc {
        options.encoding = "UTF-8"
        (options as StandardJavadocDocletOptions).charSet = "UTF-8"
        (options as StandardJavadocDocletOptions).docEncoding = "UTF-8"
    }
}
