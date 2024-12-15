rootProject.name = "emulator-tools"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
            content {
                includeGroup("com.github.yschimke")
                includeGroup("com.github.yschimke.schoutput")
            }
        }
        maven {
            url = uri("https://androidx.dev/snapshots/builds/12801052/artifacts/repository")
        }
    }
}

include(
    "tools",
    "emulator-proto",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
