plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("kapt") version "1.6.10"
    `maven-publish`
    application
    id("net.nemerosa.versioning") version "2.15.1"
    id("com.diffplug.spotless") version "5.1.0"
    id("com.palantir.graal") version "0.10.0"
    id("com.squareup.wire") version "4.0.1"
}

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.maven.apache.org/maven2")
}

group = "com.github.yschimke"
version = versioning.info.display

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "ee.schimke.emulatortools.MainKt"
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

// https://github.com/square/okio/issues/647
configurations.all {
    if (name.contains("kapt") || name.contains("proto", ignoreCase = true)) {
        attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
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
