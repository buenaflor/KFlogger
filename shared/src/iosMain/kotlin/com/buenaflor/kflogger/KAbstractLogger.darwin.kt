package com.buenaflor.kflogger

actual abstract class KAbstractLogger<API : KLoggingApi<API>> {
    /**
     * Returns a fluent logging API appropriate for the specified log KLevel.
     *
     *
     * If a logger implementation determines that logging is definitely disabled at this point then
     * this method is expected to return a "no-op" implementation of that logging API, which will
     * result in all further calls made for the log statement to being silently ignored.
     *
     *
     * A simple implementation of this method in a concrete subclass might look like:
     * <pre>`boolean isLoggable = isLoggable(KLevel);
     * boolean isForced = Platform.shouldForceLogging(getName(), KLevel, isLoggable);
     * return (isLoggable | isForced) ? new SubContext(KLevel, isForced) : NO_OP;
    `</pre> *
     * where `NO_OP` is a singleton, no-op instance of the logging API whose methods do nothing
     * and just `return noOp()`.
     */
    actual abstract fun at(level: KLevel?): API

    /** A convenience method for at([KLevel.SEVERE]).  */
    actual fun atSevere(): API {
        TODO("Not yet implemented")
    }

    /** A convenience method for at([KLevel.WARNING]).  */
    actual fun atWarning(): API {
        TODO("Not yet implemented")
    }

    /** A convenience method for at([KLevel.INFO]).  */
    actual fun atInfo(): API {
        TODO("Not yet implemented")
    }

    /** A convenience method for at([KLevel.CONFIG]).  */
    actual fun atConfig(): API {
        TODO("Not yet implemented")
    }

    /** A convenience method for at([KLevel.FINE]).  */
    actual fun atFine(): API {
        TODO("Not yet implemented")
    }

    /** A convenience method for at([KLevel.FINER]).  */
    actual fun atFiner(): API {
        TODO("Not yet implemented")
    }

    /** A convenience method for at([KLevel.FINEST]).  */
    actual fun atFinest(): API {
        TODO("Not yet implemented")
    }

    /**
     * Returns whether the given KLevel is enabled for this logger. Users wishing to guard code with a
     * check for "loggability" should use `logger.atLevel().isEnabled()` instead.
     */
    protected actual fun isLoggable(level: KLevel): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Invokes the logging backend to write a log statement, ensuring that all exceptions which could
     * be caused during logging, including any subsequent error handling, are handled. This method can
     * only fail due to instances of [LoggingException] or [Error] being thrown.
     *
     *
     * This method also guards against unbounded reentrant logging, and will suppress further
     * logging if it detects significant recursion has occurred.
     */
    actual fun write(data: KLogData?) {
        TODO()
    }

    /**
     * Returns the non-null name of this logger (Flogger does not currently support anonymous
     * loggers).
     */
    protected actual val name: String?
        get() = TODO("Not yet implemented")
}


actual val <API : KLoggingApi<API>> KAbstractLogger<API>.backend: KLoggerBackend get() = TODO()
