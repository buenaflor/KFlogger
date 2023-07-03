package com.buenaflor.kflogger

import com.buenaflor.kflogger.backend.KLogData
import com.buenaflor.kflogger.backend.KLoggerBackend

/**
 * Base class for the fluent logger API. This class is a factory for instances of a fluent logging
 * API, used to build log statements via method chaining.
 *
 * @param <API> the logging API provided by this logger. </API>
 */
expect abstract class KAbstractLogger<API : KLoggingApi<API>>() {
  // ---- PUBLIC API ----
  /**
   * Returns a fluent logging API appropriate for the specified log KLevel.
   *
   * If a logger implementation determines that logging is definitely disabled at this point then
   * this method is expected to return a "no-op" implementation of that logging API, which will
   * result in all further calls made for the log statement to being silently ignored.
   *
   * A simple implementation of this method in a concrete subclass might look like:
   * <pre>`boolean isLoggable = isLoggable(KLevel);
   * boolean isForced = Platform.shouldForceLogging(getName(), KLevel, isLoggable);
   * return (isLoggable | isForced) ? new SubContext(KLevel, isForced) : NO_OP;
   * `</pre> *
   *
   * where `NO_OP` is a singleton, no-op instance of the logging API whose methods do nothing and
   * just `return noOp()`.
   */
  abstract fun at(level: KLevel?): API

  /** A convenience method for at([KLevel.SEVERE]). */
  fun atSevere(): API

  /** A convenience method for at([KLevel.WARNING]). */
  fun atWarning(): API

  /** A convenience method for at([KLevel.INFO]). */
  fun atInfo(): API

  /** A convenience method for at([KLevel.CONFIG]). */
  fun atConfig(): API

  /** A convenience method for at([KLevel.FINE]). */
  fun atFine(): API

  /** A convenience method for at([KLevel.FINER]). */
  fun atFiner(): API

  /** A convenience method for at([KLevel.FINEST]). */
  fun atFinest(): API

  // ---- HELPER METHODS (useful during sub-class initialization) ----

  /**
   * Returns the non-null name of this logger (Flogger does not currently support anonymous
   * loggers).
   */
  // IMPORTANT: Flogger does not currently support the idea of an anonymous logger instance
  // (but probably should). The issue here is that in order to allow the FluentLogger instance
  // and the LoggerConfig instance to share the same underlying logger, while allowing the
  // backend API to be flexible enough _not_ to admit the existence of the JDK logger, we will
  // need to push the LoggerConfig API down into the backend and expose it from there.
  // See b/14878562
  // TODO(dbeaumont): Make anonymous loggers work with the config() method and the LoggerConfig API.
  protected val name: String?

  /**
   * Returns whether the given KLevel is enabled for this logger. Users wishing to guard code with a
   * check for "loggability" should use `logger.atLevel().isEnabled()` instead.
   */
  protected fun isLoggable(level: KLevel): Boolean

  // ---- IMPLEMENTATION DETAIL (only visible to the base logging context) ----

  /**
   * Invokes the logging backend to write a log statement, ensuring that all exceptions which could
   * be caused during logging, including any subsequent error handling, are handled. This method can
   * only fail due to instances of [LoggingException] or [Error] being thrown.
   *
   * This method also guards against unbounded reentrant logging, and will suppress further logging
   * if it detects significant recursion has occurred.
   */
  fun write(data: KLogData?)
}

/**
 * Returns the logging backend (not visible to logger subclasses to discourage tightly coupled
 * implementations).
 */
expect val <API : KLoggingApi<API>> KAbstractLogger<API>.backend: KLoggerBackend
