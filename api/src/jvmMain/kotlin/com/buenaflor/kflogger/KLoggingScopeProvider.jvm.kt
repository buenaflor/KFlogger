package com.buenaflor.kflogger

public actual typealias KLoggingScopeProvider = LoggingScopeProvider

public actual val KLoggingScopeProvider.currentScope: KLoggingScope? get() = currentScope