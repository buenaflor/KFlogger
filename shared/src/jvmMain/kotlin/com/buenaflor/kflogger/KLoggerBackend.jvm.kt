package com.buenaflor.kflogger

actual typealias KLoggerBackend = com.buenaflor.kflogger.backend.LoggerBackend

actual val KLoggerBackend.loggerName: String? get() = loggerName
