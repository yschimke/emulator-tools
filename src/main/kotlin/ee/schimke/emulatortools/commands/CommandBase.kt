package ee.schimke.emulatortools.commands

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Callable

abstract class CommandBase: Callable<Int> {
  override fun call(): Int {
    return runBlocking {
      callFun()
      0
    }
  }

  abstract suspend fun CoroutineScope.callFun()
}