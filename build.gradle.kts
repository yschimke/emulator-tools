plugins {
    kotlin("jvm") version "1.3.71"
    `maven-publish`
    application
    id("com.github.ben-manes.versions") version "0.28.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("net.nemerosa.versioning") version "2.8.2"
}

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.maven.apache.org/maven2")
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap/")
}

group = "com.github.yschimke"
version = versioning.info.display

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.apiVersion = "1.3"
    kotlinOptions.languageVersion = "1.3"
}

application {
    mainClassName = "ee.schimke.emulatortools.Main"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.grpc:grpc-kotlin-stub:0.1.1")

    implementation("com.google.protobuf:protobuf-gradle-plugin:0.8.11")
    implementation("com.google.protobuf:protobuf-java:3.11.1")
    implementation("com.google.protobuf:protobuf-java-util:3.11.1")
    implementation("io.grpc:grpc-netty-shaded:1.26.0")
    implementation("io.grpc:grpc-protobuf:1.26.0")
    implementation("io.grpc:grpc-stub:1.26.0")
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