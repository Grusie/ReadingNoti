plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.1.20"
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(libs.junit)

    // Serialization
    implementation(libs.kotlinx.serialization)
}