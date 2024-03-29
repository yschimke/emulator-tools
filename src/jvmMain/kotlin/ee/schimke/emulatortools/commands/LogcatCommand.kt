package ee.schimke.emulatortools.commands

import com.android.emulator.control.LogMessage
import ee.schimke.emulatortools.Main
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.receiveAsFlow
import picocli.CommandLine

@CommandLine.Command(name = "logcat", description = ["show logs"])
class LogcatCommand: CommandBase() {
  @CommandLine.ParentCommand
  lateinit var parent: Main

  override suspend fun CoroutineScope.callFun() {
    val emulatorController = parent.emulatorController ?: throw Exception("No Grpc Controller Found")
    val (request, logs) = emulatorController.streamLogcat().executeIn(this)
    request.send(LogMessage(sort = LogMessage.LogType.Parsed))
    logs.receiveAsFlow().collect {
      it.entries.forEach { log ->
        println("${log.level.name + " "}\t${log.msg}")
      }
    }
  }
}