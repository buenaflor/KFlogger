package com.giancarlobuenaflor.kflogger.backend.system

import com.giancarlobuenaflor.kflogger.KLogger
import com.giancarlobuenaflor.kflogger.backend.KLogData
import com.giancarlobuenaflor.kflogger.toOsLogType
import kotlin.native.concurrent.AtomicReference
import kotlinx.cinterop.ptr
import platform.darwin.__dso_handle
import platform.darwin._os_log_internal
import platform.darwin.os_log_create
import platform.darwin.os_log_t

/** A logging backend that uses the `OSLog` class to output log statements. */
public actual class KSimpleLoggerBackend actual constructor(public val logger: KLogger) :
    KAbstractBackend(logger) {
  public actual override fun log(data: KLogData) {
    val templateContext = data.getTemplateContext()
    if (templateContext != null) {
      if (logger.isLoggable(data.getLevel())) {
        log(data.getLevel().toOsLogType(), templateContext.message)
      }
    } else {
      log(data.getLevel().toOsLogType(), data.getLiteralArgument().toString())
    }
  }

  private fun log(osLogSeverity: UByte, message: String) {
    // TODO: KFlogger implement MessageFormatting so we can support arguments as well
    _os_log_internal(__dso_handle.ptr, logger.osLogger, osLogSeverity, message)
  }

  public actual override fun handleError(error: RuntimeException, badData: KLogData) {
    // TODO: KFlogger implement
  }
}

public class OSLogSubsystemAppender(private val subsystem: String) {
  private val logs: AtomicReference<Map<String, os_log_t>> = AtomicReference(mapOf())

  public fun logger(loggerName: String): os_log_t {
    var logger: os_log_t
    do {
      val existing = logs.value
      logger = existing[loggerName]
      if (logger != null) {
        return logger
      }

      val updated = existing.toMutableMap()
      logger = os_log_create(subsystem, loggerName)
      updated[loggerName] = logger
    } while (!logs.compareAndSet(existing, updated))

    return logger
  }
}
