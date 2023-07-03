package com.buenaflor.kflogger.backend

actual typealias KLoggerBackend = LoggerBackend

actual val KLoggerBackend.loggerName: String? get() = loggerName
