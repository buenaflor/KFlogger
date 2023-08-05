package com.buenaflor.kflogger.backend.system

import com.buenaflor.kflogger.KLogger
import com.buenaflor.kflogger.backend.KLogData
import com.buenaflor.kflogger.toOsLogType
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
    data.getTemplateContext()?.message?.let {
      if (logger.isLoggable(data.getLevel())) {
        log(data.getLevel().toOsLogType(), it)
      }
    }
  }

  private fun log(osLogSeverity: UByte, message: String) {
    _os_log_internal(__dso_handle.ptr, logger.osLogger, osLogSeverity, "%s", message)
  }

  public actual override fun handleError(error: RuntimeException, badData: KLogData) {}
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
