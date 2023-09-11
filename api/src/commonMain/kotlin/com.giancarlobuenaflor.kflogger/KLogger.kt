package com.giancarlobuenaflor.kflogger

public expect class KLogger {
  public fun isLoggable(level: KLevel): Boolean
}
