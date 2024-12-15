@file:OptIn(ExperimentalTraceProcessorApi::class)

package ee.schimke.emulatortools.perfetto

import androidx.benchmark.traceprocessor.ExperimentalTraceProcessorApi
import androidx.benchmark.traceprocessor.PerfettoTrace
import androidx.benchmark.traceprocessor.ServerLifecycleManager
import androidx.benchmark.traceprocessor.TraceProcessor
import com.github.pgreze.process.InputSource
import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.Closeable
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import java.net.ServerSocket

class LocalTraceProcessor(
  val traceProcessor: Path? = null,
  val coroutineScope: CoroutineScope
) : ServerLifecycleManager, Closeable {
  private val port: Int = ServerSocket(0).use { it.localPort }

  override fun start(): Int {
    coroutineScope.launch(Dispatchers.IO) {
      if (traceProcessor != null) {
        process(
          traceProcessor.toString(),
          "-D",
          "--http-port",
          "$port",
          stderr = Redirect.CAPTURE
        ) {
          if (it.startsWith("Failed to listen on")) {
            throw IllegalStateException(it)
          }
        }
      } else {
        FileSystem.RESOURCES.read("/trace_processor".toPath()) {
          process(
            "python3",
            "-",
            "-D",
            "--http-port",
            "$port",
            stderr = Redirect.CAPTURE,
            stdin = InputSource.fromInputStream(inputStream())
          ) {
            if (it.startsWith("Failed to listen on")) {
              throw IllegalStateException(it)
            }
          }
        }
      }
    }

    return port
  }

  override fun stop() {
    coroutineScope.cancel()
  }

  override fun close() {
    stop()
  }

  companion object {
    suspend fun runServerForQuery(
      traceFile: Path,
      traceProcessor: Path? = null,
      eventCallback: TraceProcessor.EventCallback = object : TraceProcessor.EventCallback {
        override fun onLoadTraceFailure(trace: PerfettoTrace, throwable: Throwable) {
          throwable.printStackTrace()
        }
      },
      block: TraceProcessor.Session.() -> Unit
    ): Unit = try {
      coroutineScope {
        val server = LocalTraceProcessor(traceProcessor = traceProcessor, coroutineScope = this)

        withContext(Dispatchers.IO) {
          TraceProcessor.runServer(
            serverLifecycleManager = server,
            eventCallback = eventCallback,
            tracer = TraceProcessor.Tracer()
          ) {
            loadTrace(PerfettoTrace(traceFile.toString())) {
              block()
            }
          }
        }
      }
    } catch (ce: CancellationException) {
      // expected
    }
  }
}