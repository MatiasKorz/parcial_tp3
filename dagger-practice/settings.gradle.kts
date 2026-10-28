// EXT-07: standalone Gradle build, not included from the root settings.gradle.kts, so the app and its
// Hilt graph never see it. It reuses the repo's wrapper and version catalog: `gradlew -p dagger-practice run`.
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "dagger-practice"
