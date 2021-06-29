plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
}

val http4kVersion = "4.3.3.0"
val junitVersion = "5.7.1"
fun http4k(module: String) = "org.http4k:http4k-$module:$http4kVersion"
fun junit(module: String) = "org.junit.jupiter:junit-jupiter-$module:$junitVersion"

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("io.bryter.integrations:kotlin-integrations-lib:3.0.0")

    implementation(http4k("core"))
    implementation(http4k("format-jackson"))

    testImplementation("io.bryter.integrations:kotlin-integrations-tests-lib:3.0.0")
    testImplementation(junit("engine"))
    testImplementation(junit("params"))
    testImplementation(http4k("testing-approval"))
    testImplementation("org.assertj:assertj-core:3.19.0")
}

application {
    // Define the main class for the application.
    mainClassName = "io.bryter.integration.parsedocument.AppKt"
}

tasks.withType<Test> {
    useJUnitPlatform {
        excludeTags("DockerTest")
    }
}

tasks.register<Test>("dockerTest") {
    useJUnitPlatform {
        includeTags("DockerTest")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}
