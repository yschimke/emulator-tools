plugins {
    kotlin("multiplatform")
    id("com.squareup.wire")
}

wire {
    sourcePath {
        srcDir("src/commonMain/proto")
        include("**")
    }

    kotlin {
        boxOneOfsMinSize = 100

        rpcRole = "client"
        rpcCallStyle = "suspending"
    }
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("com.squareup.wire:wire-grpc-client:4.9.9")
            }
        }
    }
}

tasks.maybeCreate("prepareKotlinIdeaImport")
    .dependsOn("generateProtos")