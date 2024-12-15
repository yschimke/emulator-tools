package ee.schimke.emulatortools.perfetto

import androidx.benchmark.traceprocessor.PerfettoTrace
import androidx.benchmark.traceprocessor.Row
import androidx.benchmark.traceprocessor.TraceProcessor

suspend fun main() = TraceProcessor.runServer {
  loadTrace(PerfettoTrace("/Users/yschimke/Downloads/sample.perfetto-trace")) {
    val result: Sequence<Row> =
      query("select ts, t.name, value from counter as c left join counter_track t on c.track_id = t.id where t.name = 'batt.current_ua'")

    lateinit var columnNames: List<String>
    result.forEachIndexed { index, row ->
      if (index == 0) {
        columnNames = row.keys.toList()
        println(columnNames.joinToString("\t"))
      }

      println(columnNames.map { row[it] }.joinToString("\t") { it.toString() })
    }
  }
}

private suspend fun TraceProcessor.Companion.runServer(block: TraceProcessor.() -> Unit) {
  LocalTraceProcessor.runServer(
    eventCallback = object : TraceProcessor.EventCallback {
      override fun onLoadTraceFailure(trace: PerfettoTrace, throwable: Throwable) {
        throwable.printStackTrace()
      }
    },
    block = block
  )
}

