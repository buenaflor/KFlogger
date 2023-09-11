package com.giancarlobuenaflor.kflogger.backend.system

import com.giancarlobuenaflor.kflogger.KLogger
import com.giancarlobuenaflor.kflogger.backend.KLogData
import com.giancarlobuenaflor.kflogger.toOsLogType
import kotlinx.cinterop.ptr
import platform.darwin.__dso_handle
import platform.darwin._os_log_internal
import platform.darwin.os_log_create
import platform.darwin.os_log_t
import kotlin.native.concurrent.AtomicReference

/** A logging backend that uses the `OSLog` class to output log statements. */
public actual class KSimpleLoggerBackend actual constructor(public val logger: KLogger) :
    KAbstractBackend(logger) {

  public actual override fun log(data: KLogData) {
    val templateContext = data.getTemplateContext()

    if (templateContext != null) {
      if (logger.isLoggable(data.getLevel())) {
        log(data.getLevel().toOsLogType(), templateContext.message.formatArgs(data.getArguments()))
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

public fun String.formatArgs(args: Array<out Any?>?): String {
  if (args == null || args.isEmpty()) {
    return this
  }
  val formattedArgs = mutableListOf<Any?>()
  var argIndex = 0
  var skipFormatChar = false

  return buildString {
    for (char in this@formatArgs) {
      if (char == '%' && argIndex < args.size) {
        val formatSpecifier = parseFormatSpecifier()
        val arg = args[argIndex]
        val formattedArg = formatArgument(arg, formatSpecifier)
        formattedArgs.add(formattedArg)
        append(formattedArg)
        argIndex++
        skipFormatChar = true
      } else {
        if (skipFormatChar) {
          skipFormatChar = false
          continue
        } else {
          append(char)
        }
      }
    }
  }
}

private fun String.parseFormatSpecifier(): String {
  val startIndex = indexOf('%')
  if (startIndex == -1 || startIndex == length - 1) {
    throw IllegalArgumentException("Invalid format string: $this")
  }

  val specifierChar = this[startIndex + 1]
  if (specifierChar != 's' && specifierChar != 'd' && specifierChar != 'f') {
    throw IllegalArgumentException("Invalid format string: $this")
  }

  return "%$specifierChar"
}

private fun formatArgument(arg: Any?, formatSpecifier: String): Any {
  if (arg == null) {
    return "null"
  }
  return when (formatSpecifier) {
    "%s" -> arg.toString()
    "%d", "%i" -> arg.toString().toIntOrNull() ?: 0
    "%f" -> arg.toString().toDoubleOrNull() ?: 0.0
    else -> arg.toString()
  }
}