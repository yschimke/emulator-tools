package ee.schimke.emulatortools.commands

import com.android.emulator.control.ImageFormat
import com.malinskiy.adam.AndroidDebugBridgeClientFactory
import com.malinskiy.adam.interactor.StartAdbInteractor
import com.malinskiy.adam.request.device.Device
import com.malinskiy.adam.request.device.ListDevicesRequest
import com.malinskiy.adam.request.framebuffer.RawImageScreenCaptureAdapter
import com.malinskiy.adam.request.framebuffer.ScreenCaptureRequest
import ee.schimke.emulatortools.ContentAndType
import ee.schimke.emulatortools.Main
import kotlinx.coroutines.CoroutineScope
import okio.Buffer
import picocli.CommandLine
import javax.imageio.ImageIO

@CommandLine.Command(name = "wearscreenshot", description = ["screenshot"])
class WearScreenshotCommand: CommandBase() {
  @CommandLine.ParentCommand
  lateinit var parent: Main

  override suspend fun CoroutineScope.callFun() {
    StartAdbInteractor().execute()
    val adb = AndroidDebugBridgeClientFactory().build()
    val devices: List<Device> = adb.execute(ListDevicesRequest())

    val adapter = RawImageScreenCaptureAdapter()
    val response = adb.execute(ScreenCaptureRequest(adapter), serial = devices.first().serial)

    val image = response.toBufferedImage()

    val pngBytes = Buffer().use {
      ImageIO.write(image, "PNG",it.outputStream())
      it.readByteString()
    }

    // https://cs.android.com/android-studio/platform/tools/adt/idea/+/mirror-goog-studio-main:android/src/com/android/tools/idea/ddms/screenshot/DeviceArtScreenshotPostprocessor.kt?q=isRoundScreen&ss=android-studio%2Fplatform%2Ftools%2Fadt%2Fidea:android%2Fsrc%2Fcom%2Fandroid%2Ftools%2Fidea%2F

    parent.console.showOutput(ContentAndType(pngBytes, mimetype = "image/png"))
  }
}