package ee.schimke.emulatortools

import picocli.CommandLine
import kotlin.system.exitProcess

fun main() {
  LoggingUtil.configureLogging()

  exitProcess(CommandLine(Main()).execute("wearscreenshot", "--display"))
}