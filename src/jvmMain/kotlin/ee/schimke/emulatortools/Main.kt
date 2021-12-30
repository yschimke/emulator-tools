package ee.schimke.emulatortools

import com.android.emulator.control.EmulatorControllerClient
import com.squareup.wire.GrpcClient
import ee.schimke.emulatortools.commands.BatteryCommand
import ee.schimke.emulatortools.commands.LogcatCommand
import ee.schimke.emulatortools.commands.ScreenshotCommand
import com.baulsupp.schoutput.outputHandlerInstance
import com.baulsupp.schoutput.responses.ResponseExtractor
import ee.schimke.emulatortools.devices.DeviceFinder
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okio.Buffer
import okio.ByteString
import picocli.CommandLine
import java.io.Closeable
import kotlin.system.exitProcess

@CommandLine.Command(
  name = "emulatortools",
  description = ["Emulator tools."],
  mixinStandardHelpOptions = true,
  subcommands = [LogcatCommand::class, BatteryCommand::class, ScreenshotCommand::class]
)
class Main : Closeable {
  @CommandLine.Option(names = ["--port"], hidden = true)
  var port: Int = 8554

  val console = outputHandlerInstance(object : ResponseExtractor<ContentAndType> {
    override fun filename(response: ContentAndType): String? = null

    override fun mimeType(response: ContentAndType): String? = response.mimetype

    override fun source(response: ContentAndType): okio.BufferedSource {
      return Buffer().write(response.content)
    }
  })

  val deviceFinder = DeviceFinder()

  val grpcClient = GrpcClient.Builder()
    .client(OkHttpClient.Builder()
      .protocols(listOf(Protocol.H2_PRIOR_KNOWLEDGE))
      .build())
    .baseUrl("http://localhost:$port")
    .build()

  val emulatorController = grpcClient.create(EmulatorControllerClient::class)

  override fun close() {
  }
}

data class ContentAndType(val content: ByteString, val filename: String? = null, val mimetype: String? = null)

fun main(args: Array<String>) {
  LoggingUtil.configureLogging()

  exitProcess(CommandLine(Main()).execute(*args))
}
