plugins {
    kotlin("multiplatform") version "1.9.21"
    kotlin("kapt") version "1.9.21"
    id("maven-publish")
    id("net.nemerosa.versioning") version "3.0.0"
    id("com.squareup.wire") version "4.9.3"
    id("org.jreleaser") version "1.2.0"
    application
}

versioning {
    scm = "git"
    releaseParser =
        KotlinClosure2<net.nemerosa.versioning.SCMInfo, String, net.nemerosa.versioning.ReleaseInfo>({ scmInfo, _ ->
            if (scmInfo.tag != null && scmInfo.tag.matches("\\d+\\.\\d+\\.\\d+".toRegex())) {
                net.nemerosa.versioning.ReleaseInfo("release", scmInfo.tag)
            } else {
                val parts = scmInfo.branch.split("/", limit = 2)
                net.nemerosa.versioning.ReleaseInfo(parts[0], parts.getOrNull(1) ?: "")
            }
        })
}

group = "com.github.yschimke"
version = versioning.info.effectiveVersion()

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

tasks.withType(Jar::class) {
    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Main-Class"] = "ee.schimke.emulatortools.MainKt"
    }
}

application {
    mainClass.set("ee.schimke.emulatortools.MainKt")
}

wire {
    sourcePath {
        srcDir("src/commonMain/proto")
        include("**")
    }

    kotlin {
        rpcRole = "client"
        rpcCallStyle = "suspending"
    }
}

afterEvaluate {
    val wireTask = tasks.withType<com.squareup.wire.gradle.WireTask>().single()
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        dependsOn(wireTask)
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
                api("com.squareup.okio:okio:3.7.0")
                api("com.github.yschimke.schoutput:schoutput:1.0.1")
                api("com.squareup.wire:wire-grpc-client:4.9.9")

                api("io.ktor:ktor-client-core:2.3.1")
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

                api("com.squareup.wire:wire-runtime:4.9.3")
                api("com.squareup.wire:wire-grpc-client:4.9.3")

                implementation("io.ktor:ktor-client-okhttp:2.3.1")
                implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
                implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.1")
                implementation("com.github.pgreze:kotlin-process:1.4.1")

                implementation("dev.mobile:dadb:1.2.6")
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

publishing {
    publications {
        withType<MavenPublication> {
            tasks.withType<AbstractPublishToMaven>()
                .matching { it.publication == this }
                .configureEach { enabled = true }
        }
        repositories {
            maven {
                url = uri("file:build/repo")
            }
        }
    }
}

jreleaser {
    dryrun.set(rootProject.booleanProperty("jreleaser.dryrun"))

    project {
        description.set("Android Emulator Tools")
        authors.set(listOf("yschimke"))
        license.set("Apache-2.0")
        copyright.set("Yuri Schimke")
    }

    release {
        github {
            repoOwner.set("yschimke")
            overwrite.set(true)
            skipTag.set(true)
        }
    }

    assemble {
        enabled.set(true)

        nativeImage {
            create("emulator-tools") {
                active.set(org.jreleaser.model.Active.ALWAYS)
                exported.set(true)

                arg("--enable-https")
                arg("--no-fallback")
                arg("--allow-incomplete-classpath")
                arg("--report-unsupported-elements-at-runtime")

                graalJdk {
                    platform.set("osx-aarch_64")
                    path.set(
                        File(
                            System.getenv("GRAALVM_HOME")
                                ?: "/Users/yschimke/Library/Java/JavaVirtualMachines/graalvm-ce-17/Contents/Home"
                        )
                    )
                }

                if (System.getenv("GRAALVM_HOME") != null) {
                    graalJdk {
                        platform.set("linux-x86_64")
                        path.set(
                            File(
                                System.getenv("GRAALVM_HOME")
                            )
                        )
                    }
                }

                mainJar {
                    path.set(File("build/libs/emulator-tools-jvm-$version.jar"))
                }

                jars {
                    directory.set(File("build/install/emulator-tools/lib"))
                    pattern.set("*.jar")
                }

                files {
                    pattern.set("LICENSE")
                }
                files {
                    pattern.set("README.md")
                }
            }
        }
    }

    packagers {
        brew {
            active.set(org.jreleaser.model.Active.RELEASE)
            repoTap {
                repoOwner.set("yschimke")
                formulaName.set("emulator-tools")
            }
        }
    }
}

fun Project.booleanProperty(name: String) = this.findProperty(name).toString().toBoolean()

fun booleanEnv(name: String) = System.getenv(name).toString().toBoolean()

task("tagRelease") {
    doLast {
        val tagName = versioning.info.nextVersion() ?: throw IllegalStateException("unable to compute tag name")
        exec {
            commandLine("git", "tag", tagName)
        }
        exec {
            commandLine("git", "push", "origin", "refs/tags/$tagName")
        }
    }
}

fun net.nemerosa.versioning.VersionInfo.nextVersion() = when {
    this.tag == null && this.branch == "main" -> {
        val matchResult = "(\\d+)\\.(\\d+)\\.(\\d+)".toRegex().matchEntire(this.lastTag ?: "")
        if (matchResult != null) {
            val (_, major, minor) = matchResult.groupValues
            "v$major.${minor.toInt() + 1}"
        } else {
            null
        }
    }

    else -> {
        null
    }
}

fun net.nemerosa.versioning.VersionInfo.effectiveVersion() = when {
    this.tag == null -> {
        val matchResult = "(\\d+)\\.(\\d+)\\.(\\d+)".toRegex().matchEntire(this.lastTag ?: "")
        if (matchResult != null) {
            val (_, major, minor) = matchResult.groupValues
            "$major.${minor.toInt() + 1}.0-SNAPSHOT"
        } else {
            "0.0.1-SNAPSHOT"
        }
    }

    else -> {
        this.display
    }
}

tasks.maybeCreate("prepareKotlinIdeaImport")
    .dependsOn("generateProtos")