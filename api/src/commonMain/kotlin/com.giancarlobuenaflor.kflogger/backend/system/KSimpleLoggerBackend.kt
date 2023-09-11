package com.giancarlobuenaflor.kflogger.backend.system

import com.giancarlobuenaflor.kflogger.KLogger
import com.giancarlobuenaflor.kflogger.backend.KLogData

/** A logging backend that uses the `java.util.logging` classes to output log statements. */
public expect class KSimpleLoggerBackend(logger: KLogger) : KAbstractBackend {
  public override fun log(data: KLogData)

  public override fun handleError(error: RuntimeException, badData: KLogData)
}
