import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.shadow)
    jacoco
}

group = "com.talestonini"
version = "0.1.0"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(libs.bundles.kotlinx.ecosystem)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.exposed)
    implementation(libs.h2)
    implementation(libs.logback.classic)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    testImplementation("net.jqwik:jqwik:1.9.2")
    testImplementation("net.jqwik:jqwik-kotlin:1.9.2")
    testImplementation("org.assertj:assertj-core:3.27.3")
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs = listOf(
            "-Xnullability-annotations=@org.jspecify.annotations:strict",
            "-Xemit-jvm-type-annotations" // Enable annotations on type variables
        )
        apiVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
        languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
        javaParameters = true // Get correct parameter names in jqwik reporting
    }
}