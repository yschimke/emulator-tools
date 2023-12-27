package ee.schimke.emulatortools.commands

import ee.schimke.emulatortools.Main
import kotlinx.coroutines.CoroutineScope
import picocli.CommandLine

@CommandLine.Command(name = "battery", description = ["show battery"])
class BatteryCommand: CommandBase() {
  @CommandLine.ParentCommand
  lateinit var parent: Main

  override suspend fun CoroutineScope.callFun() {
    val emulatorController = parent.emulatorController ?: throw Exception("No Grpc Controller Found")
    val batteryStatus = emulatorController.getBattery().execute(Unit)
    println(batteryStatus)
  }
}