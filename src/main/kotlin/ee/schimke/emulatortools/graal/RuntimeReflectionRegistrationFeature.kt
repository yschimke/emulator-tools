package ee.schimke.emulatortools.graal

import com.google.protobuf.GeneratedMessageV3
import com.oracle.svm.core.annotate.AutomaticFeature
import io.github.classgraph.ClassGraph
import org.graalvm.nativeimage.hosted.Feature
import org.graalvm.nativeimage.hosted.Feature.BeforeAnalysisAccess
import org.graalvm.nativeimage.hosted.RuntimeReflection

@AutomaticFeature
internal class RuntimeReflectionRegistrationFeature : Feature {
  override fun beforeAnalysis(access: BeforeAnalysisAccess) {
    try {
      val pkg = "com.baulsupp.cooee.cli"
      ClassGraph()
//      .verbose() // Log to stderr
        .enableClassInfo() // Scan classes, methods, fields, annotations
        .acceptPackages(pkg) // Scan com.xyz and subpackages (omit to scan all packages)
        .scan()
        .use { scanResult ->                    // Start the scan
          for (classInfo in scanResult.getSubclasses(GeneratedMessageV3::class.java.name)) {
            registerGrpcAdapter(classInfo.loadClass())
          }
        }
    } catch (e: Exception) {
      e.printStackTrace()
      throw e
    }
  }

  private fun registerGrpcAdapter(java: Class<*>) {
    RuntimeReflection.register(java)
    java.methods.forEach {
      RuntimeReflection.register(it)
    }
    java.constructors.forEach {
      RuntimeReflection.register(it)
    }
  }
}
