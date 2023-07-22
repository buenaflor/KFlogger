package com.buenaflor.kflogger.backend

public actual typealias KLoggerBackend = LoggerBackend

public actual val KLoggerBackend.loggerName: String?
    get() = loggerName