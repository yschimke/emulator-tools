package ee.schimke.emulatortools.perfetto

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import perfetto.protos.QueryArgs

suspend fun main() = perfettoSession {
    withTraceFile("/Users/yschimke/Downloads/sample.perfetto-trace".toPath()) {
        val result =
            query(QueryArgs("select ts, t.name, value from counter as c left join counter_track t on c.track_id = t.id where t.name = 'batt.current_ua'"))
                .getOrThrow()

        val columnNames = result.column_names
        println(columnNames.joinToString("\t"))
        val rows = QueryResultIterator(result).asSequence()
        rows.forEach { row ->
            println(row.entries.joinToString("\t") { it.value.toString() })
        }
    }
}

suspend fun perfettoSession(block: suspend PerfettoHttpClient.() -> Unit) {
    withContext(Dispatchers.Default) {
        val service = PerfettoHttpService()
        val server = launch {
            service.run()
        }

        val client = PerfettoHttpClient(service.url)

        client.block()

        server.cancel()
        client.close()
    }
}

suspend fun PerfettoHttpClient.withTraceFile(traceFile: Path, block: suspend PerfettoHttpClient.() -> Unit) {
    uploadFile(traceFile)

    try {
        block()
    } finally {
        clear()
    }
}

suspend fun PerfettoHttpClient.uploadFile(traceFile: Path) {
    clear()

    val uploadResult = uploadFile(traceFile).getOrThrow()
    if (uploadResult.error != null) {
        throw IOException(uploadResult.error)
    }
}

