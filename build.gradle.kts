import org.gradle.kotlin.dsl.provider.gradleKotlinDslOf

plugins {
    kotlin("jvm") version "1.4.0"
    kotlin("kapt") version "1.4.0"
    `maven-publish`
    application
    id("net.nemerosa.versioning") version "2.14.0"
    id("com.diffplug.spotless") version "5.1.0"
    id("com.squareup.wire") version "3.3.0"
    id("com.palantir.graal") version "0.7.1"
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
    kotlinOptions.allWarningsAsErrors = false
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=enable", "-Xopt-in=kotlin.RequiresOptIn")
}

application {
    mainClassName = "ee.schimke.emulatortools.MainKt"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.grpc:grpc-kotlin-stub:0.1.5")

    implementation("info.picocli:picocli:4.5.0")
    implementation("com.github.yschimke:oksocial-output:5.6")
    implementation("com.squareup.okio:okio:2.7.0")
    implementation("javax.annotation:javax.annotation-api:1.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("org.slf4j:slf4j-jdk14:2.0.0-alpha0")

    implementation("com.squareup.wire:wire-runtime:3.4.0")
    implementation("com.squareup.wire:wire-moshi-adapter:3.4.0")
    implementation("com.squareup.wire:wire-grpc-client:3.4.0") {
        exclude(group= "com.squareup.okhttp3")
    }

    kapt("info.picocli:picocli-codegen:4.5.0")
    compileOnly("org.graalvm.nativeimage:svm:20.2.0") {
        // https://youtrack.jetbrains.com/issue/KT-29513
        exclude(group= "org.graalvm.nativeimage")
        exclude(group= "org.graalvm.truffle")
//        exclude(group= "org.graalvm.sdk")
        exclude(group= "org.graalvm.compiler")
    }
    implementation("io.github.classgraph:classgraph:4.8.87")
}

sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/java")
            srcDirs("build/generated/source/proto/main/grpc")
            srcDirs("build/generated/source/proto/main/grpckt")
        }
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
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

wire {
    kotlin {
        out = "src/main/kotlin"
        javaInterop = true
    }
}

graal {
    mainClass("ee.schimke.emulatortools.MainKt")
    outputName("emulator-tools")
    graalVersion("20.2.0")
    javaVersion("11")

    option("--enable-https")
    option("--no-fallback")
    option("--allow-incomplete-classpath")
}
