plugins {
    kotlin("multiplatform") version "1.6.10"
    kotlin("kapt") version "1.6.10"
    id("maven-publish")
    id("net.nemerosa.versioning") version "2.15.0"
    id("com.squareup.wire") version "4.0.1"
    id("org.jreleaser") version "0.10.0"
    application
}

versioning {
    scm = "git"
    releaseParser = KotlinClosure2<net.nemerosa.versioning.SCMInfo, String, net.nemerosa.versioning.ReleaseInfo>({ scmInfo, _ ->
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
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js {
        compilations.all {
            kotlinOptions {
                moduleKind = "umd"
                sourceMap = true
                metaInfo = true
            }
        }
        nodejs {
            testTask {
                useMocha {
                    timeout = "30s"
                }
            }
        }
        browser {
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("com.squareup.okio:okio:3.0.0")
                api("com.github.yschimke.schoutput:schoutput:0.9.2")
                api("com.squareup.wire:wire-grpc-client:4.0.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("com.squareup.okio:okio:3.0.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                dependsOn(commonMain)

                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")

                implementation("com.github.yschimke.schoutput:schoutput:0.9.2")

                implementation("info.picocli:picocli:4.6.2")
                implementation("com.squareup.okio:okio:3.0.0")
                implementation("javax.annotation:javax.annotation-api:1.3.2")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                implementation("org.slf4j:slf4j-jdk14:2.0.0-alpha0")

                configurations["kapt"].dependencies.add(project.dependencies.create("info.picocli:picocli-codegen:4.6.2"))
                compileOnly("org.graalvm.nativeimage:svm:21.2.0")
                implementation("io.github.classgraph:classgraph:4.8.138")

                api("com.squareup.wire:wire-runtime:4.0.1")
                api("com.squareup.wire:wire-grpc-client:4.0.1")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("com.squareup.okio:okio:3.0.0")
            }
        }
        val nonJvmMain by creating {
            dependencies {
                dependsOn(commonMain)
            }
        }
        val nonJvmTest by creating {
            dependencies {
                dependsOn(commonTest)
            }
        }
        val jsMain by getting {
            dependsOn(commonMain)
            dependsOn(nonJvmMain)
            dependencies {
                implementation("com.github.yschimke.schoutput:schoutput:0.9.2")
            }
        }
        val jsTest by getting {
            dependencies {
                dependsOn(nonJvmTest)
                implementation(kotlin("test"))
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
        website.set("https://github.com/yschimke/emulator-tools")
        description.set("Android Emulator Tools")
        authors.set(listOf("yschimke"))
        license.set("Apache-2.0")
        copyright.set("Yuri Schimke")
    }

    release {
        github {
            owner.set("yschimke")
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

                addArg("--enable-https")
                addArg("--no-fallback")
                addArg("--allow-incomplete-classpath")
                addArg("--report-unsupported-elements-at-runtime")

                graal {
                    path.set(File(System.getenv("GRAALVM_HOME") ?: "/Library/Java/JavaVirtualMachines/graalvm-ce-java17-21.3.0/Contents/Home"))
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
                owner.set("yschimke")
                formulaName.set("emulator-tools")
            }
        }
    }
}

fun Project.booleanProperty(name: String) = this.findProperty(name).toString().toBoolean()

fun Project.booleanEnv(name: String) = (System.getenv(name) as String?).toString().toBoolean()

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