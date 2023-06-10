package ee.schimke.emulatortools

import com.android.emulator.control.EmulatorControllerClient
import com.baulsupp.schoutput.outputHandlerInstance
import com.baulsupp.schoutput.responses.ResponseExtractor
import com.squareup.wire.GrpcClient
import ee.schimke.emulatortools.commands.BatteryCommand
import ee.schimke.emulatortools.commands.LogcatCommand
import ee.schimke.emulatortools.commands.ScreenshotCommand
import ee.schimke.emulatortools.devices.DeviceFinder
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okio.Buffer
import okio.ByteString
import okio.FileSystem
import okio.Path.Companion.toPath
import picocli.CommandLine
import java.io.Closeable
import java.util.Properties
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

    // https://cs.android.com/android-studio/platform/tools/adt/idea/+/mirror-goog-studio-main:streaming/src/com/android/tools/idea/streaming/emulator/RunningEmulatorCatalog.kt

    val grpcClient = GrpcClient.Builder()
        .client(OkHttpClient.Builder()
            .protocols(listOf(Protocol.H2_PRIOR_KNOWLEDGE))
            .addInterceptor {
                it.proceed(addEmulatorAuth(it.request()))
            }
            .build())
        .baseUrl("http://localhost:$port")
        .build()

    private fun addEmulatorAuth(request: Request): Request {
        val config = findConfig(request.url.port)
        return if (config != null) {
            request.newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer ${config["grpc.token"]}"
                )
                .build()
        } else {
            request
        }
    }

    val homeDir = System.getenv("HOME").toPath()
    val runningDir = homeDir / "Library/Caches/TemporaryItems/avd/running/"

    private fun findConfig(port: Int): Properties? {
        return FileSystem.SYSTEM.list(runningDir).filter {
            it.name.matches("pid_\\d+.ini".toRegex())
        }.asSequence().map {
            Properties().apply {
                FileSystem.SYSTEM.read(it) {
                    load(inputStream())
                }
            }
        }.find { it["grpc.port"] == port.toString() }
    }

    val emulatorController = grpcClient.create(EmulatorControllerClient::class)

    override fun close() {
    }
}

data class ContentAndType(val content: ByteString, val filename: String? = null, val mimetype: String? = null)

fun main(args: Array<String>) {
    LoggingUtil.configureLogging()

  exitProcess(CommandLine(Main()).execute(*args))
}
