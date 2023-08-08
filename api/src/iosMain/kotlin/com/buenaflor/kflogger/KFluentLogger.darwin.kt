package com.buenaflor.kflogger

import com.buenaflor.kflogger.backend.KLoggerBackend
import com.buenaflor.kflogger.backend.KPlatform
import com.buenaflor.kflogger.parser.KMessageBuilder
import com.buenaflor.kflogger.parser.KMessageParser

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
public actual class KFluentLogger(backend: KLoggerBackend) :
    KAbstractLogger<KFluentLoggerApi>(backend) {
  actual override fun at(level: KLevel): KFluentLoggerApi {
    val isLoggable = isLoggable(level)
    // val isForced: Boolean = KPlatform.shouldForceLogging(getName(), level, isLoggable)
    // TODO: check if isForced
    return if (isLoggable) Context(level, false) else NO_OP
  }

  /** Logging context implementing the fully specified API for this logger. */
  internal inner class Context internal constructor(level: KLevel, isForced: Boolean) :
      KLogContext<KFluentLogger, KFluentLoggerApi>(level, isForced), KFluentLoggerApi {

    override fun getLogger(): KFluentLogger {
      return this@KFluentLogger
    }

    override fun noOp(): KFluentLoggerApi {
      return this@KFluentLogger.atFine()
    }

    override fun getMessageParser(): KMessageParser {
      return NoOpMessageParser()
    }

    override fun api(): KFluentLoggerApi {
      return this
    }
  }

  /**
   * The non-wildcard, fully specified, no-op API implementation. This is required to provide a
   * no-op implementation whose type is compatible with this logger's API.
   */
  internal class NoOp : KLoggingApiNoOp<KFluentLoggerApi>(), KFluentLoggerApi

  public actual companion object {
    // Singleton instance of the no-op API. This variable is purposefully declared as an instance of
    // the NoOp type instead of the Api type. This helps ProGuard optimization recognize the type of
    // this field more easily. This allows ProGuard to strip away low-level logs in Android apps in
    // fewer optimization passes. Do not change this to 'Api', or any less specific type.
    internal val NO_OP: NoOp = NoOp()

    /**
     * Returns a new logger instance which parses log messages using printf format for the enclosing
     * class using the system default logging backend.
     */
    public actual fun forEnclosingClass(): KFluentLogger {
      val loggingClass: String =
          KPlatform.getCallerFinder().findLoggingClass(KFluentLogger::class.toKlass())
      return KFluentLogger(KPlatform.getBackend(loggingClass))
    }
  }

  // TODO KFlogger: this is only a temporary implementation to allow compilation of KFluentLogger
  private class NoOpMessageParser : KMessageParser() {
    override fun <T> parseImpl(builder: KMessageBuilder<T>?) {}

    override fun unescape(out: StringBuilder?, message: String?, start: Int, end: Int) {}
  }
}
