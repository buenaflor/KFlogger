package com.buenaflor.kflogger

/**
 * The default implementation of [AbstractLogger] which returns the basic [LoggingApi] and uses the
 * default parser and system configured backend.
 *
 * Note that when extending the logging API or specifying a new parser, you will need to create a
 * new logger class (rather than extending this one). Unlike the [LogContext] class, which must be
 * extended in order to modify the logging API, this class is not generified and thus cannot be
 * modified to produce a different logging API.
 *
 * The choice to prevent direct extension of loggers was made deliberately to ensure that users of a
 * specific logger implementation always get the same behavior.
 */
public expect class FluentLogger : AbstractLogger<FluentLogger.Api> {

  public interface Api : LoggingApi<Api>

  override fun at(level: Level?): Api

  public companion object {
    /**
     * Returns a new logger instance which parses log messages using printf format for the enclosing
     * class using the system default logging backend.
     */
    public fun forEnclosingClass(): FluentLogger
  }
}
