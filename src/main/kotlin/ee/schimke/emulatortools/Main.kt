package ee.schimke.emulatortools

import com.android.emulator.control.EmulatorControllerGrpcKt
import com.android.emulator.control.ImageFormat
import com.android.emulator.control.LogMessage
import com.baulsupp.oksocial.output.ConsoleHandler
import com.baulsupp.oksocial.output.ResponseExtractor
import com.google.protobuf.Empty
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.toByteString
import picocli.CommandLine
import java.io.Closeable
import java.util.concurrent.TimeUnit
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

    val console = ConsoleHandler.instance(object: ResponseExtractor<ContentAndType> {
        override fun filename(response: ContentAndType): String? = null

        override fun mimeType(response: ContentAndType): String? = response.mimetype

        override fun source(response: ContentAndType): okio.BufferedSource {
            return Buffer().write(response.content)
        }
    })

    val channel = lazy { ManagedChannelBuilder.forTarget("localhost:8554").usePlaintext().build() }

    val client by lazy { EmulatorControllerGrpcKt.EmulatorControllerCoroutineStub(channel.value) }

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

    suspend fun showLogs() {
        val request = LogMessage.newBuilder().apply {
            sort = LogMessage.LogType.Parsed
        }.build()
        val logcat = client.streamLogcat(request)
        logcat.collect {
            it.entriesList.forEach {
                val level = it.level.name + " "
                println("$level\t${it.msg}")
            }
        }
    }

    suspend fun showBattery() {
        println(client.getBattery(Empty.getDefaultInstance()))
    }

    suspend fun screenshot() {
        val format = ImageFormat.newBuilder().apply {
            format = ImageFormat.ImgFormat.PNG
        }.build()
        val screenshot = client.getScreenshot(format)
        console.showOutput(ContentAndType(screenshot.image.toByteArray().toByteString(), mimetype = "image/png"))
    }

    override fun close() {
        if (channel.isInitialized()) {
            channel.value.shutdown().awaitTermination(5, TimeUnit.SECONDS)
        }
    }
}

data class ContentAndType(val content: ByteString, val filename: String? = null, val mimetype: String? = null)

@InternalCoroutinesApi
fun main(args: Array<String>) {
    LoggingUtil.configureLogging()

    exitProcess(CommandLine(Main()).execute(*args))
}
