package com.buenaflor.kflogger.backend

import com.buenaflor.kflogger.KLevel
import com.buenaflor.kflogger.KLogSite

actual typealias KLogData = LogData

actual val KLogData.metadata: KMetadata?
  get() = metadata

actual val KLogData.level: KLevel?
  get() = level

actual val KLogData.timestampMicros: Long
  get() = timestampMicros

actual val KLogData.timestampNanos: Long
  get() = timestampNanos

actual val KLogData.loggerName: String?
  get() = loggerName

actual val KLogData.logSite: KLogSite?
  get() = logSite

actual val KLogData.arguments: Array<Any?>?
  get() = arguments

actual val KLogData.literalArgument: Any?
  get() = literalArgument
