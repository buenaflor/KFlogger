package com.buenaflor.kflogger


/**
 * Base class for the fluent logger API. This class is a factory for instances of a fluent logging
 * API, used to build log statements via method chaining.
 *
 * @param <API> the logging API provided by this logger.
</API> */
expect abstract class KAbstractLogger<API : KLoggingApi<API>?>{

    // ---- PUBLIC API ----
    /**
     * Returns a fluent logging API appropriate for the specified log level.
     *
     *
     * If a logger implementation determines that logging is definitely disabled at this point then
     * this method is expected to return a "no-op" implementation of that logging API, which will
     * result in all further calls made for the log statement to being silently ignored.
     *
     *
     * A simple implementation of this method in a concrete subclass might look like:
     * <pre>`boolean isLoggable = isLoggable(level);
     * boolean isForced = Platform.shouldForceLogging(getName(), level, isLoggable);
     * return (isLoggable | isForced) ? new SubContext(level, isForced) : NO_OP;
    `</pre> *
     * where `NO_OP` is a singleton, no-op instance of the logging API whose methods do nothing
     * and just `return noOp()`.
     */
    abstract fun at(level: KLevel?): API

    /** A convenience method for at([KLevel.SEVERE]).  */
    fun atSevere(): API

    /** A convenience method for at([KLevel.WARNING]).  */
    fun atWarning(): API

    /** A convenience method for at([KLevel.INFO]).  */
    fun atInfo(): API

    /** A convenience method for at([KLevel.CONFIG]).  */
    fun atConfig(): API

    /** A convenience method for at([KLevel.FINE]).  */
    fun atFine(): API

    /** A convenience method for at([KLevel.FINER]).  */
    fun atFiner(): API

    /** A convenience method for at([KLevel.FINEST]).  */
    fun atFinest(): API

    //TODO: Helper methods
}