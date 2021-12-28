package ee.schimke.emulatortools.commands

import ee.schimke.emulatortools.Main
import kotlinx.coroutines.CoroutineScope
import picocli.CommandLine

@CommandLine.Command(name = "battery", description = ["show battery"])
class BatteryCommand: CommandBase() {
  @CommandLine.ParentCommand
  lateinit var parent: Main

  override suspend fun CoroutineScope.callFun() {
    val batteryStatus = parent.client.getBattery().execute(Unit)
    println(batteryStatus)
  }
}