package com.buenaflor.kflogger

actual abstract class KAbstractLogger<API : KLoggingApi<API>?> {
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
}