package com.buenaflor.kflogger

import com.buenaflor.kflogger.backend.KLogData
import com.buenaflor.kflogger.backend.KLoggerBackend

public actual abstract class KAbstractLogger<API : KLoggingApi<API>>
protected actual constructor(private val backend: KLoggerBackend) {
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
  public actual abstract fun at(level: KLevel?): API

  /** A convenience method for at([Level.SEVERE]). */
  public actual fun atSevere(): API {
    TODO("Not yet implemented")
  }

  /** A convenience method for at([Level.WARNING]). */
  public actual fun atWarning(): API {
    return at(KLevel.WARNING)
  }

  /** A convenience method for at([Level.INFO]). */
  public actual fun atInfo(): API {
    TODO("Not yet implemented")
  }

  /** A convenience method for at([Level.CONFIG]). */
  public actual fun atConfig(): API {
    TODO("Not yet implemented")
  }

  /** A convenience method for at([Level.FINE]). */
  public actual fun atFine(): API {
    TODO("Not yet implemented")
  }

  /** A convenience method for at([Level.FINER]). */
  public actual fun atFiner(): API {
    TODO("Not yet implemented")
  }

  /** A convenience method for at([Level.FINEST]). */
  public actual fun atFinest(): API {
    TODO("Not yet implemented")
  }

  /**
   * Returns the non-null name of this logger (Flogger does not currently support anonymous
   * loggers).
   */
  public actual val name: String
    get() = TODO("Not yet implemented")

  /**
   * Returns whether the given level is enabled for this logger. Users wishing to guard code with a
   * check for "loggability" should use `logger.atLevel().isEnabled()` instead.
   */
  public actual fun isLoggable(level: KLevel?): Boolean {
    TODO("Not yet implemented")
  }

  /**
   * Invokes the logging backend to write a log statement, ensuring that all exceptions which could
   * be caused during logging, including any subsequent error handling, are handled. This method can
   * only fail due to instances of [LoggingException] or [Error] being thrown.
   *
   * This method also guards against unbounded reentrant logging, and will suppress further logging
   * if it detects significant recursion has occurred.
   */
  public actual fun write(data: KLogData) {
    // TODO KFlogger
    backend.log(data)
  }
}
