package com.buenaflor.kflogger

public expect class KLogger {
  public fun isLoggable(level: KLevel): Boolean
}
