package com.buenaflor.kflogger

interface KFluentLoggerApi : KLoggingApi<KFluentLoggerApi>

/**
 * The non-wildcard, fully specified, no-op API implementation. This is required to provide a
 * no-op implementation whose type is compatible with this logger's API.
 */
// TODO: internal class KFluentLoggerNoOp: KLoggingApiNoOp<KFluentLoggerApi>(), KFluentLoggerApi

/**
 * The default implementation of [KAbstractLogger] which returns the basic [KLoggingApi]
 * and uses the default parser and system configured backend.
 *
 *
 * Note that when extending the logging API or specifying a new parser, you will need to create a
 * new logger class (rather than extending this one). Unlike the [LogContext] class,
 * which must be extended in order to modify the logging API, this class is not generified and thus
 * cannot be modified to produce a different logging API.
 *
 *
 * The choice to prevent direct extension of loggers was made deliberately to ensure that users of
 * a specific logger implementation always get the same behavior.
 */
expect class KFluentLogger : KAbstractLogger<KFluentLoggerApi> {

    override fun at(level: KLevel?): KFluentLoggerApi

    companion object {
        // Singleton instance of the no-op API. This variable is purposefully declared as an instance of
        // the NoOp type instead of the Api type. This helps ProGuard optimization recognize the type of
        // this field more easily. This allows ProGuard to strip away low-level logs in Android apps in
        // fewer optimization passes. Do not change this to 'Api', or any less specific type.
        // VisibleForTesting
        // TODO: internal val NO_OP: KFluentLoggerNoOp

        /**
         * Returns a new logger instance which parses log messages using printf format for the enclosing
         * class using the system default logging backend.
         */
        fun forEnclosingClass(): KFluentLogger
    }
}
