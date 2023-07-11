package com.buenaflor.kflogger

import com.buenaflor.kflogger.backend.LogData
import com.buenaflor.kflogger.backend.LoggerBackend

/**
 * Base class for the fluent logger API. This class is a factory for instances of a fluent logging
 * API, used to build log statements via method chaining.
 *
 * @param <API> the logging API provided by this logger. </API>
 */
public expect abstract class AbstractLogger<API : LoggingApi<API>>
protected constructor(backend: LoggerBackend) {
  /**
   * Returns the logging backend (not visible to logger subclasses to discourage tightly coupled
   * implementations).
   */
  public val backend: LoggerBackend

  // ---- PUBLIC API ----
  /**
   * Returns a fluent logging API appropriate for the specified log level.
   *
   * If a logger implementation determines that logging is definitely disabled at this point then
   * this method is expected to return a "no-op" implementation of that logging API, which will
   * result in all further calls made for the log statement to being silently ignored.
   *
   * A simple implementation of this method in a concrete subclass might look like:
   * <pre>`boolean isLoggable = isLoggable(level);
   * boolean isForced = Platform.shouldForceLogging(getName(), level, isLoggable);
   * return (isLoggable | isForced) ? new SubContext(level, isForced) : NO_OP;
   * `</pre> *
   *
   * where `NO_OP` is a singleton, no-op instance of the logging API whose methods do nothing and
   * just `return noOp()`.
   */
  public abstract fun at(level: Level?): API

  /** A convenience method for at([Level.SEVERE]). */
  public fun atSevere(): API

  /** A convenience method for at([Level.WARNING]). */
  public fun atWarning(): API

  /** A convenience method for at([Level.INFO]). */
  public fun atInfo(): API

  /** A convenience method for at([Level.CONFIG]). */
  public fun atConfig(): API

  /** A convenience method for at([Level.FINE]). */
  public fun atFine(): API

  /** A convenience method for at([Level.FINER]). */
  public fun atFiner(): API

  /** A convenience method for at([Level.FINEST]). */
  public fun atFinest(): API

  // ---- HELPER METHODS (useful during sub-class initialization) ----
  /**
   * Returns the non-null name of this logger (Flogger does not currently support anonymous
   * loggers).
   */
  public val name: String

  /**
   * Returns whether the given level is enabled for this logger. Users wishing to guard code with a
   * check for "loggability" should use `logger.atLevel().isEnabled()` instead.
   */
  public fun isLoggable(level: Level?): Boolean

  // ---- IMPLEMENTATION DETAIL (only visible to the base logging context) ----
  /**
   * Invokes the logging backend to write a log statement, ensuring that all exceptions which could
   * be caused during logging, including any subsequent error handling, are handled. This method can
   * only fail due to instances of [LoggingException] or [Error] being thrown.
   *
   * This method also guards against unbounded reentrant logging, and will suppress further logging
   * if it detects significant recursion has occurred.
   */
  public fun write(data: LogData)
}
