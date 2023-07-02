package com.buenaflor.kflogger

actual class KFluentLogger: KAbstractLogger<Api>() {

    /**
     * The non-wildcard, fully specified, no-op API implementation. This is required to provide a
     * no-op implementation whose type is compatible with this logger's API.
     */
    // TODO: This should be private but cannot be inside an expect
    //actual class NoOp : KLoggingApi.NoOp<Api>(), Api

    actual companion object {
        // Singleton instance of the no-op API. This variable is purposefully declared as an instance of
        // the NoOp type instead of the Api type. This helps ProGuard optimization recognize the type of
        // this field more easily. This allows ProGuard to strip away low-level logs in Android apps in
        // fewer optimization passes. Do not change this to 'Api', or any less specific type.
        // VisibleForTesting


        /**
         * Returns a new logger instance which parses log messages using printf format for the enclosing
         * class using the system default logging backend.
         */
        actual fun forEnclosingClass(): KFluentLogger {
            TODO("Not yet implemented")
        }
    }

    actual override fun at(level: KLevel?): Api {
        TODO("Not yet implemented")
    }
}