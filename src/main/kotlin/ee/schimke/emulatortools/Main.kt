package ee.schimke.emulatortools

import com.android.emulator.control.EmulatorControllerClient
import com.android.emulator.control.ImageFormat
import com.android.emulator.control.LogMessage
import com.baulsupp.oksocial.output.ConsoleHandler
import com.baulsupp.oksocial.output.ResponseExtractor
import com.squareup.wire.GrpcClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.toByteString
import picocli.CommandLine
import java.io.Closeable
import kotlin.system.exitProcess

@InternalCoroutinesApi
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

//    val httpClient = OkHttpClient()

  val grpcClient = GrpcClient.Builder()
//    .client(okHttpClient)
    .baseUrl("http://localhost:$port")
    .build()

  val client by lazy { grpcClient.create(EmulatorControllerClient::class) }

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

  @OptIn(ExperimentalCoroutinesApi::class)
  suspend fun showLogs() {
    coroutineScope {
      val (requests, responses) = client.streamLogcat().executeIn(this)

      requests.send(LogMessage(sort = LogMessage.LogType.Parsed))

      responses.consumeEach {
        it.entries.forEach { entry ->
          val level = entry.level.name + " "
          println("$level\t${entry.msg}")
        }
      }
    }
  }

  @Suppress("BlockingMethodInNonBlockingContext")
  suspend fun showBattery() {
    val batteryStatus = client.getBattery().execute(Unit())
    println(batteryStatus)
  }

  @Suppress("BlockingMethodInNonBlockingContext")
  suspend fun screenshot() {
    val screenshot = client.getScreenshot().execute(ImageFormat(format = ImageFormat.ImgFormat.PNG))
    console.showOutput(ContentAndType(screenshot.image.toByteArray().toByteString(), mimetype = "image/png"))
  }

  override fun close() {
  }
}

data class ContentAndType(val content: ByteString, val filename: String? = null, val mimetype: String? = null)

@InternalCoroutinesApi
fun main(args: Array<String>) {
  LoggingUtil.configureLogging()

  exitProcess(CommandLine(Main()).execute(*args))
}
