package ee.schimke.emulatortools.graal

import com.oracle.svm.core.annotate.AutomaticFeature
import com.squareup.wire.Service
import io.github.classgraph.ClassGraph
import org.graalvm.nativeimage.hosted.Feature
import org.graalvm.nativeimage.hosted.Feature.BeforeAnalysisAccess
import org.graalvm.nativeimage.hosted.RuntimeReflection

@AutomaticFeature
internal class RuntimeReflectionRegistrationFeature : Feature {
  override fun beforeAnalysis(access: BeforeAnalysisAccess) {
    try {
      ClassGraph()
        .enableClassInfo()
        .acceptPackages("com")
        .scan()
        .use { scanResult ->
          for (classInfo in scanResult.getClassesImplementing(Service::class.java.name)) {
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
