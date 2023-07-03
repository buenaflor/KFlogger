package com.buenaflor.kflogger

actual class KFluentLogger: KAbstractLogger<KFluentLoggerApi>() {

    actual companion object {
        // Singleton instance of the no-op API. This variable is purposefully declared as an instance of
        // the NoOp type instead of the Api type. This helps ProGuard optimization recognize the type of
        // this field more easily. This allows ProGuard to strip away low-level logs in Android apps in
        // fewer optimization passes. Do not change this to 'Api', or any less specific type.
        // VisibleForTesting
        // TODO: internal actual val NO_OP: KFluentLoggerNoOp = KFluentLoggerNoOp()

        /**
         * Returns a new logger instance which parses log messages using printf format for the enclosing
         * class using the system default logging backend.
         */
        actual fun forEnclosingClass(): KFluentLogger {
            TODO("Not yet implemented")
        }
    }

    actual override fun at(level: KLevel?): KFluentLoggerApi {
        TODO("Not yet implemented")
    }
}
