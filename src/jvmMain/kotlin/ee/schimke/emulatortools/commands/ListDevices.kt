package ee.schimke.emulatortools.commands

import ee.schimke.emulatortools.Main
import ee.schimke.emulatortools.devices.DeviceFinder
import kotlinx.coroutines.CoroutineScope
import picocli.CommandLine

@CommandLine.Command(name = "devices", description = ["list devices"])
class ListDevices: CommandBase() {
  @CommandLine.ParentCommand
  lateinit var main: Main

  override suspend fun CoroutineScope.callFun() {
    main.deviceFinder.findEmulators().forEach(::println)
  }
}