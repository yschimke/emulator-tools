package ee.schimke.emulatortools.commands

import com.android.emulator.control.ImageFormat
import ee.schimke.emulatortools.ContentAndType
import ee.schimke.emulatortools.Main
import kotlinx.coroutines.CoroutineScope
import picocli.CommandLine

@CommandLine.Command(name = "screenshot", description = ["screenshot"])
class ScreenshotCommand: CommandBase() {
  @CommandLine.ParentCommand
  lateinit var parent: Main

  override suspend fun CoroutineScope.callFun() {
    val format = ImageFormat(format = ImageFormat.ImgFormat.PNG)
    val screenshot = parent.client.getScreenshot().execute(format)
    parent.console.showOutput(ContentAndType(screenshot.image, mimetype = "image/png"))
  }
}