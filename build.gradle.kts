plugins {
    kotlin("multiplatform") version "1.9.21" apply false
    id("maven-publish")
    id("net.nemerosa.versioning") version "3.0.0"
    id("com.squareup.wire") version "4.9.9" apply false
    id("org.jreleaser") version "1.2.0" apply false
}

versioning {
    scm = "git"
//    releaseParser =
//        KotlinClosure2<net.nemerosa.versioning.SCMInfo, kotlin.String, net.nemerosa.versioning.ReleaseInfo>({ scmInfo, _ ->
//            if (scmInfo.tag != null && scmInfo.tag.matches("\\d+\\.\\d+\\.\\d+".toRegex())) {
//                net.nemerosa.versioning.ReleaseInfo("release", scmInfo.tag)
//            } else {
//                val parts = scmInfo.branch.split("/", limit = 2)
//                net.nemerosa.versioning.ReleaseInfo(parts[0], parts.getOrNull(1) ?: "")
//            }
//        })
}

allprojects {
    group = "com.github.yschimke"
//    version = versioning.info.effectiveVersion()
}