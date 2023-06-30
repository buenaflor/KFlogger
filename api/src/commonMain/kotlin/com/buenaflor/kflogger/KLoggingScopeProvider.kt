package com.buenaflor.kflogger

/**
 * Provides a scope to a log statement via the [LogContext.per] method.
 *
 *
 * This interface exists to avoid needing to pass specific instances of [LoggingScope]
 * around in user code. The scope provider can lookup the correct scope instance for the current
 * thread, and different providers can provide different types of scope (e.g. you can have a
 * provider for "request" scopes and a provider for "sub-task" scopes)
 */
expect interface KLoggingScopeProvider

expect val KLoggingScopeProvider.currentScope: KLoggingScope?