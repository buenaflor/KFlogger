/*
 * Copyright (C) 2014 The Flogger Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.giancarlobuenaflor.kflogger.backend.system

import com.giancarlobuenaflor.kflogger.KLevel
import com.giancarlobuenaflor.kflogger.KLogRecord
import com.giancarlobuenaflor.kflogger.KLogger
import com.giancarlobuenaflor.kflogger.backend.KLoggerBackend

// TODO KFlogger
private fun fromLoggingClass(loggingClass: String): KLogger {
  val appender = OSLogSubsystemAppender(loggingClass)
  val logger = appender.logger(loggingClass)
  return KLogger(logger)
}

/**
 * Common backend to handle everything except formatting of log message and metadata. This is an
 * unstable implementation and should not be used outside of the Flogger core library.
 */
public actual abstract class KAbstractBackend
internal actual constructor(private val logger: KLogger) : KLoggerBackend() {

  protected actual constructor(loggingClass: String) : this(fromLoggingClass(loggingClass))

  public actual final override fun isLoggable(level: KLevel): Boolean {
    return logger.isLoggable(level)
  }

  /**
   * Logs the given record using this backend. If `wasForced` is set, the backend will make a best
   * effort attempt to bypass any log level restrictions in the underlying Java [Logger], but there
   * are circumstances in which this can fail.
   */
  public actual fun log(record: KLogRecord, wasForced: Boolean) {
    TODO()
  }

  actual final override fun getLoggerName(): String {
    TODO("Not yet implemented")
  }
}
