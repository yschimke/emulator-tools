package ee.schimke.emulatortools

import com.android.emulator.control.EmulatorControllerClient
import com.baulsupp.oksocial.output.ConsoleHandler
import com.baulsupp.oksocial.output.ResponseExtractor
import com.squareup.wire.GrpcClient
import ee.schimke.emulatortools.commands.BatteryCommand
import ee.schimke.emulatortools.commands.LogcatCommand
import ee.schimke.emulatortools.commands.ScreenshotCommand
import ee.schimke.emulatortools.commands.WearScreenshotCommand
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
  subcommands = [LogcatCommand::class, BatteryCommand::class, ScreenshotCommand::class, WearScreenshotCommand::class]
)
class Main : Closeable {
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

  val emulatorController = grpcClient.create(EmulatorControllerClient::class)

  override fun close() {
  }
}

data class ContentAndType(val content: ByteString, val filename: String? = null, val mimetype: String? = null)

fun main(args: Array<String>) {
  LoggingUtil.configureLogging()

  exitProcess(CommandLine(Main()).execute(*args))
}
