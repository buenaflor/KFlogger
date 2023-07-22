package com.buenaflor.kflogger.backend

import com.buenaflor.kflogger.KLevel
import com.buenaflor.kflogger.KLogSite

public actual typealias KLogData = LogData

public actual val KLogData.metadata: KMetadata?
  get() = metadata

public actual val KLogData.level: KLevel?
  get() = level

public actual val KLogData.timestampMicros: Long
  get() = timestampMicros

public actual val KLogData.timestampNanos: Long
  get() = timestampNanos

public actual val KLogData.loggerName: String?
  get() = loggerName

public actual val KLogData.logSite: KLogSite?
  get() = logSite

public actual val KLogData.arguments: Array<Any?>?
  get() = arguments

public actual val KLogData.literalArgument: Any?
  get() = literalArgument
