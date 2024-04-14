@file:OptIn(InternalCoroutinesApi::class)

package ee.schimke.emulatortools.perfetto

import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.net.ServerSocket

class PerfettoHttpService(
    val port: Int = ServerSocket(0).use { it.localPort }
) {

    val url: HttpUrl
        get() = "http://localhost:$port".toHttpUrl()

    // https://android.googlesource.com/platform/external/perfetto/+/master/tools/trace_processor
    suspend fun run() = withContext(Dispatchers.IO) {
        process("bin/trace_processor", "-D", "--http-port", "$port", stderr = Redirect.CAPTURE) {
            if (it.startsWith("Failed to listen on")) {
                throw IllegalStateException(it)
            }
        }
    }
}

suspend fun main() {
    PerfettoHttpService().run()
}