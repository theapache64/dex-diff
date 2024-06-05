plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("kapt") version "1.7.10" // TODO: Use single version number
    application
}

group = "com.github.theapache64.dexdiff"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    } // Add jitpack
}

dependencies {

    // Cyclone
    implementation("com.github.theapache64:cyclone:1.0.0-alpha02")

    // Dagger : A fast dependency injector for Android and Java.
    val daggerVersion = "2.44.2"
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")

    // Test deps
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("com.github.theapache64.dexdiff.app.AppKt")
}