package ee.schimke.emulatortools

import com.android.emulator.control.EmulatorControllerClient
import com.android.emulator.control.ImageFormat
import com.android.emulator.control.LogMessage
import com.baulsupp.oksocial.output.ConsoleHandler
import com.baulsupp.oksocial.output.ResponseExtractor
import com.squareup.wire.GrpcClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.toByteString
import picocli.CommandLine
import java.io.Closeable
import kotlin.system.exitProcess

@CommandLine.Command(name = "emulatortools", description = ["Emulator tools."], mixinStandardHelpOptions = true)
class Main : Runnable, Closeable {
  @CommandLine.Option(names = ["--battery"], description = ["Show battery"])
  var battery: Boolean = false

  @CommandLine.Option(names = ["--logs"], description = ["Show logs"])
  var logs: Boolean = false

  @CommandLine.Option(names = ["--screenshot"], description = ["Show screenshot"])
  var screenshot: Boolean = false

  @CommandLine.Option(names = ["--port"], hidden = true)
  var port: Int = 8554

  val console = ConsoleHandler.instance(object : ResponseExtractor<ContentAndType> {
    override fun filename(response: ContentAndType): String? = null

    override fun mimeType(response: ContentAndType): String? = response.mimetype

    override fun source(response: ContentAndType): okio.BufferedSource {
      return Buffer().write(response.content)
    }
  })

  val grpcClient = GrpcClient.Builder()
    .client(OkHttpClient.Builder()
      .protocols(listOf(Protocol.H2_PRIOR_KNOWLEDGE))
      .build())
    .baseUrl("http://localhost:$port")
    .build()

  val client = grpcClient.create(EmulatorControllerClient::class)

  override fun run() {
    runBlocking {
      if (battery) {
        showBattery()
      }

      if (logs) {
        showLogs()
      }

      if (screenshot) {
        screenshot()
      }
    }
  }

  suspend fun showLogs() = withContext(Dispatchers.Default) {
    val (request, logs) = client.streamLogcat().executeIn(this)
    request.send(LogMessage(sort = LogMessage.LogType.Parsed))
    logs.receiveAsFlow().collect {
      it.entries.forEach { log ->
        println("${log.level.name + " "}\t${log.msg}")
      }
    }
  }

  suspend fun showBattery() {
    val batteryStatus = client.getBattery().execute(Unit)
    println(batteryStatus)
  }

  suspend fun screenshot() {
    val format = ImageFormat(format = ImageFormat.ImgFormat.PNG)
    val screenshot = client.getScreenshot().execute(format)
    console.showOutput(ContentAndType(screenshot.image, mimetype = "image/png"))
  }

  override fun close() {
  }
}

data class ContentAndType(val content: ByteString, val filename: String? = null, val mimetype: String? = null)

fun main(args: Array<String>) {
  LoggingUtil.configureLogging()

  exitProcess(CommandLine(Main()).execute(*args))
}
