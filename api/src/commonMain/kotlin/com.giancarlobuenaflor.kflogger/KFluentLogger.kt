package com.giancarlobuenaflor.kflogger

import com.giancarlobuenaflor.kflogger.backend.KLoggerBackend

// This is a workaround for nested classes/interfaces not being accessible through typealiases
// See: https://youtrack.jetbrains.com/issue/KT-34281
public interface KFluentLoggerApi : KLoggingApi<KFluentLoggerApi>

/**
 * The default implementation of [KAbstractLogger] which returns the basic [KLoggingApi] and uses
 * the default parser and system configured backend.
 *
 * Note that when extending the logging API or specifying a new parser, you will need to create a
 * new logger class (rather than extending this one). Unlike the [LogContext] class, which must be
 * extended in order to modify the logging API, this class is not generified and thus cannot be
 * modified to produce a different logging API.
 *
 * The choice to prevent direct extension of loggers was made deliberately to ensure that users of a
 * specific logger implementation always get the same behavior.
 */
public expect class KFluentLogger internal constructor(backend: KLoggerBackend) :
    KAbstractLogger<KFluentLoggerApi> {
  override fun at(level: KLevel): KFluentLoggerApi

  public companion object {
    /**
     * Returns a new logger instance which parses log messages using printf format for the enclosing
     * class using the system default logging backend.
     */
    public fun forEnclosingClass(): KFluentLogger
  }
}
