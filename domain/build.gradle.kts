plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib"))
    testImplementation(libs.junit)
    implementation(libs.kotlinx.flow)
}