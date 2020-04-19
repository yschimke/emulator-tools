package ee.schimke.emulatortools

import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

object LoggingUtil {
    private val activeLoggers = mutableListOf<Logger>()

    fun configureLogging() {
        LogManager.getLogManager().reset()
        getLogger("").level = Level.SEVERE
    }

    fun getLogger(name: String): Logger {
        val logger = Logger.getLogger(name)
        activeLoggers.add(logger)
        return logger
    }
}
