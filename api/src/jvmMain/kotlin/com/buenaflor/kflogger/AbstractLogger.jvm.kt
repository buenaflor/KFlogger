/*
 * Copyright (C) 2012 The Flogger Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.buenaflor.kflogger

import com.buenaflor.kflogger.backend.LogData
import com.buenaflor.kflogger.backend.LoggerBackend
import com.buenaflor.kflogger.backend.LoggingException
import com.buenaflor.kflogger.backend.MessageUtils
import com.buenaflor.kflogger.util.Checks
import com.buenaflor.kflogger.util.RecursionDepth
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Level

/**
 * Base class for the fluent logger API. This class is a factory for instances of a fluent logging
 * API, used to build log statements via method chaining.
 *
 * @param <API> the logging API provided by this logger. </API>
 */
public actual abstract class AbstractLogger<API : LoggingApi<API>>
protected actual constructor(backend: LoggerBackend) {
  /**
   * Returns the logging backend (not visible to logger subclasses to discourage tightly coupled
   * implementations).
   */
  public actual val backend: LoggerBackend

  /**
   * Constructs a new logger for the specified backend.
   *
   * @param backend the logger backend which ultimately writes the log statements out.
   */
  init {
    this.backend = Checks.checkNotNull(backend, "backend")
  }

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
  public actual abstract fun at(level: Level?): API

  /** A convenience method for at([Level.SEVERE]). */
  public actual fun atSevere(): API {
    return at(Level.SEVERE)
  }

  /** A convenience method for at([Level.WARNING]). */
  public actual fun atWarning(): API {
    return at(Level.WARNING)
  }

  /** A convenience method for at([Level.INFO]). */
  public actual fun atInfo(): API {
    return at(Level.INFO)
  }

  /** A convenience method for at([Level.CONFIG]). */
  public actual fun atConfig(): API {
    return at(Level.CONFIG)
  }

  /** A convenience method for at([Level.FINE]). */
  public actual fun atFine(): API {
    return at(Level.FINE)
  }

  /** A convenience method for at([Level.FINER]). */
  public actual fun atFiner(): API {
    return at(Level.FINER)
  }

  /** A convenience method for at([Level.FINEST]). */
  public actual fun atFinest(): API {
    return at(Level.FINEST)
  }

  // ---- HELPER METHODS (useful during sub-class initialization) ----
  /**
   * Returns the non-null name of this logger (Flogger does not currently support anonymous
   * loggers).
   */
  public actual val name: String
    get() = backend.loggerName

  /**
   * Returns whether the given level is enabled for this logger. Users wishing to guard code with a
   * check for "loggability" should use `logger.atLevel().isEnabled()` instead.
   */
  public actual fun isLoggable(level: Level?): Boolean {
    return backend.isLoggable(level)
  }

  // ---- IMPLEMENTATION DETAIL (only visible to the base logging context) ----
  /**
   * Invokes the logging backend to write a log statement, ensuring that all exceptions which could
   * be caused during logging, including any subsequent error handling, are handled. This method can
   * only fail due to instances of [LoggingException] or [Error] being thrown.
   *
   * This method also guards against unbounded reentrant logging, and will suppress further logging
   * if it detects significant recursion has occurred.
   */
  public actual fun write(data: LogData) {
    Checks.checkNotNull(data, "data")
    // Note: Recursion checking should not be in the LoggerBackend. There are many backends and they
    // can call into other backends. We only want the counter incremented per log statement.
    try {
      RecursionDepth.enterLogStatement().use { depth ->
        if (depth.value <= MAX_ALLOWED_RECURSION_DEPTH) {
          backend.log(data)
        } else {
          reportError("unbounded recursion in log statement", data)
        }
      }
    } catch (logError: RuntimeException) {
      handleErrorRobustly(logError, data)
    }
  }

  /** Only allow LoggingException and Errors to escape this method. */
  private fun handleErrorRobustly(logError: RuntimeException, data: LogData) {
    try {
      backend.handleError(logError, data)
    } catch (allowed: LoggingException) {
      // Bypass the catch-all if the exception is deliberately created during error handling.
      throw allowed
    } catch (badError: RuntimeException) {
      // Don't trust exception toString() method here.
      reportError(badError.javaClass.name + ": " + badError.message, data)
      // However printStackTrace() will invoke toString() on the exception and its causes.
      try {
        badError.printStackTrace(System.err)
      } catch (ignored: RuntimeException) {
        // We already printed the base error so it doesn't seem worth doing anything more here.
      }
    }
  }

  public companion object {
    /**
     * An upper bound on the depth of reentrant logging allowed by Flogger. Logger backends may
     * choose to react to reentrant logging sooner than this, but once this value is reached, a
     * warning is is emitted to stderr, which will not include any user provided arguments or
     * metadata (in an attempt to halt recursion).
     */
    private const val MAX_ALLOWED_RECURSION_DEPTH = 100

    // It is important that this code never risk calling back to a user supplied value (e.g. logged
    // arguments or metadata) since that could trigger a recursive error state.
    private fun reportError(message: String, data: LogData) {
      val out = StringBuilder()
      out.append(formatTimestampIso8601(data)).append(": logging error [")
      MessageUtils.appendLogSite(data.logSite, out)
      out.append("]: ").append(message)
      System.err.println(out)
      // We expect System.err to be an auto-flushing stream, but let's be sure.
      System.err.flush()
    }

    // JDK7, no Joda-Time.
    private fun formatTimestampIso8601(data: LogData): String {
      // Sadly in JDK7, we don't have access to java.time and can't depend on things like Joda-Time.
      val timestamp = Date(TimeUnit.NANOSECONDS.toMillis(data.timestampNanos))
      // Use the system timezone here since we don't know how logger backends want to format dates.
      // The only alternative is to always use UTC, but that may cause confusion to some users, and
      // if users really want UTC, they can set that as the system timezone.
      //
      // ISO format from https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html.
      // Note that ending with "SSSXXX" would be more correct, but Android does not support this
      // until
      // v24+ (https://developer.android.com/reference/java/text/SimpleDateFormat.html).
      //
      // DO NOT attempt to cache the formatter instance as it's not thread safe, and this code is
      // not
      // performance sensitive.
      return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(timestamp)
    }
  }
}
