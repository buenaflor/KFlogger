package com.buenaflor.kflogger.backend

import com.buenaflor.kflogger.KLevel

public actual abstract class KLoggerBackend {
  /**
   * Returns whether logging is enabled for the given level for this backend. Different backends may
   * return different values depending on the class with which they are associated.
   */
  public actual abstract fun isLoggable(lvl: KLevel): Boolean

  /**
   * Outputs the log statement represented by the given [KLogData] instance.
   *
   * @param data user and logger supplied data to be rendered in a backend specific way. References
   *   to `data` must not be held after the [log] invocation returns.
   */
  public actual abstract fun log(data: KLogData?)

  /**
   * Handles an error in a log statement. Errors passed into this method are expected to have only
   * three distinct causes:
   * 1. Bad format strings in log messages (e.g. `"foo=%Q"`. These will always be instances of
   *    [ParseException][com.google.common.flogger.parser.ParseException] and contain human readable
   *    error messages describing the problem.
   * 1. A backend optionally choosing not to handle errors from user code during formatting. This is
   *    not recommended (see below) but may be useful in testing or debugging.
   * 1. Runtime errors in the backend itself.
   *
   * It is recommended that backend implementations avoid propagating exceptions in user code (e.g.
   * calls to `toString()`), as the nature of logging means that log statements are often only
   * enabled when debugging. If errors were propagated up into user code, enabling logging to look
   * for the cause of one issue could trigger previously unknown bugs, which could then seriously
   * hinder debugging the original issue.
   *
   * Typically a backend would handle an error by logging an alternative representation of the "bad"
   * log data, being careful not to allow any more exceptions to occur. If a backend chooses to
   * propagate an error (e.g. when testing or debugging) it must wrap it in [ ] to avoid it being
   * re-caught.
   *
   * @param error the exception throw when `badData` was initially logged.
   * @param badData the original `LogData` instance which caused an error. It is not expected that
   *   simply trying to log this again will succeed and error handlers must be careful in how they
   *   handle this instance, its arguments and metadata. References to `badData` must not be held
   *   after the [handleError] invocation returns.
   * @throws LoggingException to indicate an error which should be propagated into user code.
   */
  public actual abstract fun handleError(error: RuntimeException, badData: KLogData)

  /**
   * Returns the logger name (which is usually a canonicalized class name) or `null` if not given.
   */
  public actual abstract fun getLoggerName(): String?
}
