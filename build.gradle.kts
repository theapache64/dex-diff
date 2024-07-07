plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("kapt") version "1.7.10" // TODO: Use single version number
    application
}

group = "com.github.theapache64.dexdiff"
version = "1.0-SNAPSHOT"

repositories {
    google()
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

    // JadX
    val jadxVersion = "1.5.0"
    implementation("io.github.skylot:jadx-core:$jadxVersion")
    implementation("io.github.skylot:jadx-dex-input:$jadxVersion")
    implementation("io.github.skylot:jadx-java-input:$jadxVersion")

    // DiffUtils
    implementation("io.github.java-diff-utils:java-diff-utils:4.12")

    // SL4J
    implementation("org.slf4j:slf4j-nop:2.0.7")

    // Test deps
    testImplementation(kotlin("test"))
    testImplementation("com.github.theapache64:expekt:1.0.0")

    // To calculate md5
    implementation("commons-codec:commons-codec:1.17.0")
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