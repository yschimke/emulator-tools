@file:OptIn(ExperimentalTraceProcessorApi::class)

package ee.schimke.emulatortools.perfetto

import androidx.benchmark.traceprocessor.ExperimentalTraceProcessorApi
import androidx.benchmark.traceprocessor.ServerLifecycleManager
import androidx.benchmark.traceprocessor.TraceProcessor
import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.Closeable
import java.net.ServerSocket

class LocalTraceProcessor(val coroutineScope: CoroutineScope) : ServerLifecycleManager, Closeable {
  private val port: Int = ServerSocket(0).use { it.localPort }

  override fun start(): Int {
    println("start")
    coroutineScope.launch(Dispatchers.IO) {
      println("process")
      process("bin/trace_processor", "-D", "--http-port", "$port", stderr = Redirect.CAPTURE) {
        println("$it")
        if (it.startsWith("Failed to listen on")) {
          throw IllegalStateException(it)
        }
      }
    }

    return port
  }

  override fun stop() {
    println("stop")
    coroutineScope.cancel()
  }

  override fun close() {
    stop()
  }

  companion object {
    suspend fun runServer(
      eventCallback: TraceProcessor.EventCallback,
      block: TraceProcessor.() -> Unit
    ): Unit = coroutineScope {
      println("runServer")
      val server = LocalTraceProcessor(this)

      withContext(Dispatchers.IO) {
        println("TraceProcessor.runServer")
        TraceProcessor.runServer(
          serverLifecycleManager = server,
          eventCallback = eventCallback,
          tracer = TraceProcessor.Tracer(), block = block
        )
      }
    }
  }
}