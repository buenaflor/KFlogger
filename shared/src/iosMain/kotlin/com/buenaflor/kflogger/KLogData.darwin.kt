package com.buenaflor.kflogger

actual interface KLogData {
    /** Returns the log level for the current log statement.  */
    actual fun getLevel(): KLevel?
    actual fun getTimestampMicros(): Long

    /** Returns a nanosecond timestamp for the current log statement.  */
    actual fun getTimestampNanos(): Long

    /**
     * Returns the logger name (which is usually a canonicalized class name) or `null` if not
     * given.
     */
    actual fun getLoggerName(): String?

    /**
     * Returns the log site data for the current log statement.
     *
     * @throws IllegalStateException if called prior to the postProcess() method being called.
     */
    actual fun getLogSite(): KLogSite?

    /**
     * Returns whether this log statement should be emitted regardless of its log level or any other
     * properties.
     *
     *
     * This allows extensions of `LogContext` or `LoggingBackend` which implement
     * additional filtering or rate-limiting fluent methods to easily check whether a log statement
     * was forced. Forced log statements should behave exactly as if none of the filtering or
     * rate-limiting occurred, including argument validity checks.
     *
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
    `</pre> *
     *
     *
     * Checking for forced log statements before checking the validity of arguments provides a
     * last-resort means to mitigate cases in which syntactically incorrect log statements are only
     * discovered when they are enabled.
     */
    actual fun wasForced(): Boolean

    /**
     * Returns the arguments to be formatted with the message. Arguments exist when a `log()`
     * method with a format message and separate arguments was invoked.
     *
     * @throws IllegalStateException if no arguments are available (ie, when there is no template
     * context).
     */
    actual fun getArguments(): Array<Any?>?

    /**
     * Returns the single argument to be logged directly when no arguments were provided.
     *
     * @throws IllegalStateException if no single literal argument is available (ie, when a template
     * context exists).
     */
    actual fun getLiteralArgument(): Any?
}