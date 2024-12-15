package ee.schimke.emulatortools.perfetto

import androidx.benchmark.traceprocessor.Row
import kotlinx.coroutines.runBlocking
import okio.Closeable
import okio.Path.Companion.toPath
import picocli.CommandLine

@CommandLine.Command(
  name = "perfettoquery",
  description = ["Perfetto Query"],
  mixinStandardHelpOptions = true,
)
class PerfettoQueryMain: Closeable, Runnable {
  @CommandLine.Option(names = ["--traceProcessor", "-p"])
  var traceProcessor: String? = null

  @CommandLine.Option(names = ["--traceFile", "-t"], required = true)
  lateinit var traceFile: String

  @CommandLine.Option(names = ["--query", "-q"], required = true)
  lateinit var query: String

  override fun run() = runBlocking {
    LocalTraceProcessor.runServerForQuery(
      traceFile = traceFile.toPath(),
      traceProcessor = traceProcessor?.toPath()
    ) {
      val result: Sequence<Row> = query(query)

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

  override fun close() {
  }
}

fun main(vararg args: String) {
  CommandLine(PerfettoQueryMain()).execute(*args)
}

