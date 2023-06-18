package ee.schimke.emulatortools.perfetto

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.logging.HttpLoggingInterceptor
import okio.FileSystem
import okio.Path
import perfetto.protos.AppendTraceDataResult
import perfetto.protos.QueryArgs
import perfetto.protos.QueryResult

class PerfettoHttpClient(val ktor: HttpClient) {
  constructor(url: HttpUrl): this(buildHttpClient(url))
  constructor(port: Int): this("http://localhost:$port".toHttpUrl())

  suspend fun clear() {
    runCatching {
      ktor.get("/restore_initial_tables") {
        contentType(xProtobuf)
      }
    }
  }
  suspend fun query(args: QueryArgs): Result<QueryResult> = rawQuery(args)

  private suspend inline fun <reified T> rawQuery(args: QueryArgs): Result<T> = runCatching {
      ktor.post("/query") {
        contentType(xProtobuf)
        setBody(args)
      }.body<T>()
    }

  suspend fun uploadFile(traceFile: Path): Result<AppendTraceDataResult> {
    // TODO chunk to 64MB
    val bytes = FileSystem.SYSTEM.read(traceFile) {
      readByteArray()
    }
    return runCatching {
      ktor.post("/parse") {
        contentType(ContentType.parse("application/octet-stream"))
        setBody(bytes)
      }.body<AppendTraceDataResult>()
    }
  }

  fun close() {
    ktor.close()
  }

  companion object {
    val xProtobuf = ContentType.parse("application/x-protobuf")


    fun buildHttpClient(url: HttpUrl, debug: Boolean = false): HttpClient = HttpClient(OkHttp) {
      expectSuccess = true

      engine {
        if (debug) {
          addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(
              HttpLoggingInterceptor.Level.BODY
            )
          })
        }
      }

      defaultRequest {
        url(url.toString())
      }

      install(ContentNegotiation) {
        register(xProtobuf, ProtoConverter())
      }
    }
  }
}