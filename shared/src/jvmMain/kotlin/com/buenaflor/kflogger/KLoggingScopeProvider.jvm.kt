package com.buenaflor.kflogger

actual typealias KLoggingScopeProvider = LoggingScopeProvider

actual val KLoggingScopeProvider.currentScope: KLoggingScope?
    get() = currentScope