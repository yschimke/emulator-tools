import net.nemerosa.versioning.ReleaseInfo
import net.nemerosa.versioning.VersionInfo

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("kapt") version "1.6.10"
    `maven-publish`
    application
    id("net.nemerosa.versioning") version "2.15.1"
    id("com.diffplug.spotless") version "5.1.0"
    id("com.palantir.graal") version "0.10.0"
    id("com.squareup.wire") version "4.0.1"
    id("org.jreleaser") version "0.9.1"
}

versioning {
    scm = "git"
    releaseParser = KotlinClosure2<net.nemerosa.versioning.SCMInfo, String, ReleaseInfo>({ scmInfo, _ ->
        if (scmInfo.tag != null && scmInfo.tag.startsWith("v")) {
            ReleaseInfo("release", scmInfo.tag.substring(1))
        } else {
            val parts = scmInfo.branch.split("/", limit = 2)
            ReleaseInfo(parts[0], parts.getOrNull(1) ?: "")
        }
    })
}

tasks.test {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.yschimke")
        }
    }
}

group = "com.github.yschimke"
version = versioning.info.effectiveVersion()

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("ee.schimke.emulatortools.MainKt")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.grpc:grpc-kotlin-stub:1.2.0")

    implementation("info.picocli:picocli:4.6.2")
    implementation("com.github.yschimke:oksocial-output:5.6")
    implementation("com.squareup.okio:okio:3.0.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.slf4j:slf4j-jdk14:2.0.0-alpha0")

    kapt("info.picocli:picocli-codegen:4.6.2")
    compileOnly("org.graalvm.nativeimage:svm:21.2.0")
    implementation("io.github.classgraph:classgraph:4.8.138")

    api("com.squareup.wire:wire-runtime:4.0.1")
    api("com.squareup.wire:wire-grpc-client:4.0.1")

    implementation("org.apache.commons:commons-imaging:1.0-alpha2")
    implementation("net.imagej:ij:1.53h")
    implementation("org.openimaj:core-image:1.3.10")
}

wire {
    sourcePath {
        srcDir("src/main/proto")
        include("**")
    }

    kotlin {
        rpcRole = "client"
        rpcCallStyle = "suspending"
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    repositories {
        maven(url = "build/repository")
    }
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}

graal {
    mainClass("ee.schimke.emulatortools.MainKt")
    outputName("emulator-tools")
    graalVersion("21.3.0")
    javaVersion("11")

    option("--enable-https")
    option("--no-fallback")
    option("--allow-incomplete-classpath")
    option("--report-unsupported-elements-at-runtime")
}

val nativeImage = tasks["nativeImage"]
val isJitpack = rootProject.booleanEnv("JITPACK")

if (!isJitpack) {
    distributions {
        create("graal") {
            contents {
                from("${rootProject.projectDir}") {
                    include("README.md", "LICENSE")
                }
                from("${rootProject.projectDir}/zsh") {
                    into("zsh")
                }
                into("bin") {
                    from(nativeImage)
                }
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

    this.distributions.create("emulator-tools") {
        active.set(org.jreleaser.model.Active.RELEASE)
        distributionType.set(org.jreleaser.model.Distribution.DistributionType.NATIVE_IMAGE)
        artifact {
            platform.set("osx")
            path.set(file("build/distributions/emulator-tools-graal-$version.zip"))
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

fun VersionInfo.nextVersion() = when {
    this.tag == null && this.branch == "main" -> {
        val matchResult = Regex("v(\\d+)\\.(\\d+)(?:.\\d+)").matchEntire(this.lastTag ?: "")
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

fun VersionInfo.effectiveVersion() = when {
    this.tag == null && this.branch == "main" -> {
        val matchResult = Regex("v(\\d+)\\.(\\d+)").matchEntire(this.lastTag ?: "")
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
