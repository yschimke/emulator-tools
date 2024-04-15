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
    }
}

include(
    "tools",
    "perfetto-proto",
    "emulator-proto"
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
