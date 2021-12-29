package ee.schimke.emulatortools.commands

import com.android.emulator.control.ImageFormat
import ee.schimke.emulatortools.ContentAndType
import ee.schimke.emulatortools.Main
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Buffer
import okio.buffer
import okio.sink
import picocli.CommandLine
import java.awt.Color
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB
import java.io.File
import java.time.LocalTime
import javax.imageio.ImageIO


@CommandLine.Command(name = "wearscreenshot", description = ["wear play store screenshot"])
class WearScreenshotCommand : CommandBase() {
  @CommandLine.ParentCommand
  lateinit var parent: Main

  @CommandLine.Option(names = ["--display"])
  var display: Boolean = false

  @CommandLine.Option(names = ["-o"])
  var output: File? = null

  override suspend fun CoroutineScope.callFun() = withContext(Dispatchers.IO) {
    val format = ImageFormat(format = ImageFormat.ImgFormat.PNG)
    val screenshot = parent.emulatorController.getScreenshot().execute(format)

    val buffer = Buffer().write(screenshot.image)
    val rawPicture: BufferedImage =
      ImageIO.read(buffer.inputStream()) ?: throw IllegalStateException("unable to read image")

    val cleanPicture = BufferedImage(rawPicture.width, rawPicture.height, TYPE_INT_RGB)
    val graphics = cleanPicture.createGraphics()
    graphics.clip = Ellipse2D.Float(0f, 0f, rawPicture.width.toFloat(), rawPicture.height.toFloat())
    graphics.drawImage(rawPicture, 0, 0, Color.BLACK, null)

    buffer.clear()
    ImageIO.write(cleanPicture, "PNG", buffer.outputStream())

    val content = buffer.readByteString()

    if (display) {
      parent.console.showOutput(ContentAndType(content, mimetype = "image/png"))
    }

    val file = output ?: File("screenshot-${LocalTime.now()}.png")
    file.sink().buffer().use {
      it.write(content)
    }
    println("Wrote to $file")
  }
}