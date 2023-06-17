package ee.schimke.emulatortools.perfetto

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import okio.Path.Companion.toPath
import perfetto.protos.QueryArgs

suspend fun main() {
    val client = buildClient()

    client.clear()

    val uploadResult = client.uploadFile("/Users/yschimke/Downloads/3b9b76cb-e56c-4eda-bbbe-748e3d54f07c".toPath()).getOrThrow()
    if (uploadResult.error != null) {
        throw IOException(uploadResult.error)
    }

    val result =
        client.query(QueryArgs("select ts, t.name, value from counter as c left join counter_track t on c.track_id = t.id where t.name = 'batt.current_ua'"))
            .getOrThrow()

    println(result.column_names.joinToString("\t"))
    result.batch.forEach {
        println(it)
        println(it.is_last_batch)
    }
}

private fun buildClient(): PerfettoHttpService {
    val ktor = HttpClient(OkHttp) {
        expectSuccess = true

        engine {
            addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(
                    HttpLoggingInterceptor.Level.BODY
                )
            })
        }

        defaultRequest {
            url("http://localhost:8081")
        }

        install(ContentNegotiation) {
            register(PerfettoHttpService.xProtobuf, ProtoConverter())
        }
    }

    return PerfettoHttpService(ktor)
}