package com.buenaflor.kflogger.backend

import com.buenaflor.kflogger.KLevel
import com.buenaflor.kflogger.KLogSite

actual typealias KLogData = LogData

/** Returns the log level for the current log statement.  */
actual val KLogData.level: KLevel? get() = level

/**
 * @deprecated Use timestampNanos
 */
actual val KLogData.timestampMicros: Long get() = timestampMicros

/** Returns a nanosecond timestamp for the current log statement.  */
actual val KLogData.timestampNanos: Long get() = timestampNanos

/**
 * Returns the logger name (which is usually a canonicalized class name) or `null` if not
 * given.
 */
actual val KLogData.loggerName: String? get() = loggerName

/**
 * Returns the log site data for the current log statement.
 *
 * @throws IllegalStateException if called prior to the postProcess() method being called.
 */
actual val KLogData.logSite: KLogSite? get() = logSite

/**
 * Returns the arguments to be formatted with the message. Arguments exist when a `log()`
 * method with a format message and separate arguments was invoked.
 *
 * @throws IllegalStateException if no arguments are available (ie, when there is no template
 * context).
 */
actual val KLogData.arguments: Array<Any?>? get() = arguments

/**
 * Returns the single argument to be logged directly when no arguments were provided.
 *
 * @throws IllegalStateException if no single literal argument is available (ie, when a template
 * context exists).
 */
actual val KLogData.literalArgument: Any? get() = literalArgument
