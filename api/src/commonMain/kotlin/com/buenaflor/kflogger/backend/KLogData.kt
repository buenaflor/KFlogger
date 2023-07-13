package com.buenaflor.kflogger.backend

import com.buenaflor.kflogger.KLevel
import com.buenaflor.kflogger.KLogSite

/**
 * A backend API for determining metadata associated with a log statement.
 *
 * Some metadata is expected to be available for all log statements (such as the log level or a
 * timestamp) whereas other data is optional (class/method name for example). As well providing the
 * common logging metadata, customized loggers can choose to add arbitrary key/value pairs to the
 * log data. It is up to each logging backend implementation to decide how it interprets this data
 * using the hierarchical key. See [Metadata].
 */
public expect interface KLogData {
  /** Returns the log level for the current log statement. */
  public val level: KLevel?

  @get:Deprecated("Use getTimestampNanos()") public val timestampMicros: Long

  /** Returns a nanosecond timestamp for the current log statement. */
  public val timestampNanos: Long

  /**
   * Returns the logger name (which is usually a canonicalized class name) or `null` if not given.
   */
  public val loggerName: String?

  /**
   * Returns the log site data for the current log statement.
   *
   * @throws IllegalStateException if called prior to the postProcess() method being called.
   */
  public val logSite: KLogSite?

  /**
   * Returns any additional metadata for this log statement. If no additional metadata is present,
   * the immutable empty metadata instance is returned.
   *
   * IMPORTANT: The returned instance is restricted to metadata added at the log site, and will not
   * include any scoped metadata to be applied to the log statement. To process combined log site
   * and scoped metadata, obtain or create a [MetadataProcessor].
   */
  public val metadata: KMetadata?

  /**
   * Returns whether this log statement should be emitted regardless of its log level or any other
   * properties.
   *
   * This allows extensions of `LogContext` or `LoggingBackend` which implement additional filtering
   * or rate-limiting fluent methods to easily check whether a log statement was forced. Forced log
   * statements should behave exactly as if none of the filtering or rate-limiting occurred,
   * including argument validity checks.
   *
   * Thus the idiomatic use of `wasForced()` is:
   * <pre>`public API someFilteringMethod(int value) {
   * if (wasForced()) {
   * return api();
   * }
   * if (value < 0) {
   * throw new IllegalArgumentException("Bad things ...");
   * }
   * // rest of method...
   * }
   * `</pre> *
   *
   * Checking for forced log statements before checking the validity of arguments provides a
   * last-resort means to mitigate cases in which syntactically incorrect log statements are only
   * discovered when they are enabled.
   */
  public fun wasForced(): Boolean

  /**
   * Returns a template key for this log statement, or `null` if the statement does not require
   * formatting (in which case the message to be logged can be determined by calling
   * [.getLiteralArgument]).
   */
  // TODO KFlogger: val templateContext: TemplateContext?

  /**
   * Returns the arguments to be formatted with the message. Arguments exist when a `log()` method
   * with a format message and separate arguments was invoked.
   *
   * @throws IllegalStateException if no arguments are available (ie, when there is no template
   *   context).
   */
  public val arguments: Array<Any?>?

  /**
   * Returns the single argument to be logged directly when no arguments were provided.
   *
   * @throws IllegalStateException if no single literal argument is available (ie, when a template
   *   context exists).
   */
  public val literalArgument: Any?
}
