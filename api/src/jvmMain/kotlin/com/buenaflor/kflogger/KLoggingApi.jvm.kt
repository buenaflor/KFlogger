package com.buenaflor.kflogger

import com.buenaflor.kflogger.backend.KLoggerBackend

actual typealias KLoggingApi<API> = LoggingApi<API>

actual val <API : KLoggingApi<API>> KAbstractLogger<API>.backend: KLoggerBackend
  get() = backend

