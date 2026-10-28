plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    application
}

// Bytecode Java 17, like :app, compiled with the JDK that runs Gradle (no toolchain download).
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.dagger)
    // Dagger generates DaggerPracticeComponent at compile time; KSP, as Hilt does in :app.
    ksp(libs.dagger.compiler)
    testImplementation(libs.junit)
}

application {
    mainClass.set("com.example.daggerpractice.MainKt")
}
