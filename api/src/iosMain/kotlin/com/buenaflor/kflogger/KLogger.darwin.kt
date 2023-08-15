package com.buenaflor.kflogger

import platform.darwin.*

public actual class KLogger(public val osLogger: os_log_t) {
  public actual fun isLoggable(level: KLevel): Boolean {
    // TODO: KFlogger check if levels make sense
    if (level == KLevel.OFF) return false
    if (level == KLevel.ALL) return true
    return os_log_type_enabled(osLogger, level.toOsLogType())
  }
}

// TODO KFlogger check if levels make sense
public fun KLevel.toOsLogType(): UByte {
  return when (this) {
    KLevel.SEVERE -> OS_LOG_TYPE_FAULT
    KLevel.WARNING -> OS_LOG_TYPE_ERROR
    KLevel.INFO -> OS_LOG_TYPE_INFO
    KLevel.CONFIG -> OS_LOG_TYPE_DEFAULT
    KLevel.FINE,
    KLevel.FINER,
    KLevel.FINEST -> OS_LOG_TYPE_DEBUG
    else -> OS_LOG_TYPE_DEFAULT
  }
}
