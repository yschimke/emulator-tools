plugins {
    kotlin("multiplatform")
    application
}

tasks.withType(Jar::class) {
    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Main-Class"] = "ee.schimke.emulatortools.MainKt"
    }
}

application {
    mainClass.set("ee.schimke.emulatortools.MainKt")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "21"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("com.squareup.okio:okio:3.7.0")
                api("com.github.yschimke.schoutput:schoutput:1.0.1")
                api("com.squareup.wire:wire-grpc-client:4.9.9")

                api("io.ktor:ktor-client-core:2.3.1")
                api("androidx.benchmark:benchmark-traceprocessor:1.4.0-SNAPSHOT")
                api("androidx.annotation:annotation:1.9.0-SNAPSHOT")

                api(projects.emulatorProto)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("com.squareup.okio:okio:3.7.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.21")

                implementation("com.github.yschimke.schoutput:schoutput:1.0.1")
                implementation("javax.activation:activation:1.1.1")

                implementation("info.picocli:picocli:4.7.5")
                implementation("com.squareup.okio:okio:3.7.0")
                implementation("javax.annotation:javax.annotation-api:1.3.2")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.slf4j:slf4j-jdk14:2.0.9")

                compileOnly("org.graalvm.nativeimage:svm:23.1.1")
                implementation("io.github.classgraph:classgraph:4.8.165")

                api("com.squareup.wire:wire-runtime:4.9.9")
                api("com.squareup.wire:wire-grpc-client:4.9.9")

                implementation("io.ktor:ktor-client-okhttp:2.3.1")
                implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
                implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.1")
                implementation("com.github.pgreze:kotlin-process:1.5")

                implementation("dev.mobile:dadb:1.2.7")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("com.squareup.okio:okio:3.7.0")
            }
        }
    }
}
