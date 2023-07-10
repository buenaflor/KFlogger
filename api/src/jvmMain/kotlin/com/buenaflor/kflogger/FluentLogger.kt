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

import com.buenaflor.kflogger.backend.LoggerBackend
import com.buenaflor.kflogger.backend.Platform
import com.buenaflor.kflogger.parser.DefaultPrintfMessageParser
import com.buenaflor.kflogger.parser.MessageParser
import java.util.logging.Level

/**
 * The default implementation of [AbstractLogger] which returns the basic [LoggingApi]
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
class FluentLogger
internal constructor(backend: LoggerBackend?) : AbstractLogger<FluentLogger.Api?>(backend!!) {
    /**
     * The non-wildcard, fully specified, logging API for this logger. Fluent logger implementations
     * should specify a non-wildcard API like this with which to generify the abstract logger.
     *
     *
     * It is possible to add methods to this logger-specific API directly, but it is recommended that
     * a separate top-level API and LogContext is created, allowing it to be shared by other
     * implementations.
     */
    interface Api : LoggingApi<Api?>

    /**
     * The non-wildcard, fully specified, no-op API implementation. This is required to provide a
     * no-op implementation whose type is compatible with this logger's API.
     */
    internal class NoOp : LoggingApi.NoOp<Api?>(), Api

    override fun at(level: Level?): Api {
        val isLoggable = isLoggable(level)
        val isForced = Platform.shouldForceLogging(name, level, isLoggable)
        return if (isLoggable || isForced) Context(level, isForced) else NO_OP
    }

    /** Logging context implementing the fully specified API for this logger.  */
    // VisibleForTesting
    internal inner class Context(level: Level?, isForced: Boolean) :
        LogContext<FluentLogger?, Api?>(level, isForced),
        Api {
        override fun getLogger(): FluentLogger {
            return this@FluentLogger
        }

        override fun api(): Api {
            return this
        }

        override fun noOp(): Api {
            return NO_OP
        }

        override fun getMessageParser(): MessageParser {
            return DefaultPrintfMessageParser.getInstance()
        }
    }

    companion object {
        // Singleton instance of the no-op API. This variable is purposefully declared as an instance of
        // the NoOp type instead of the Api type. This helps ProGuard optimization recognize the type of
        // this field more easily. This allows ProGuard to strip away low-level logs in Android apps in
        // fewer optimization passes. Do not change this to 'Api', or any less specific type.
        // VisibleForTesting
        @JvmField
        internal val NO_OP = NoOp()

        /**
         * Returns a new logger instance which parses log messages using printf format for the enclosing
         * class using the system default logging backend.
         */
        @JvmStatic
        fun forEnclosingClass(): FluentLogger {
            // NOTE: It is _vital_ that the call to "caller finder" is made directly inside the static
            // factory method. See getCallerFinder() for more information.
            val loggingClass = Platform.getCallerFinder().findLoggingClass(
                FluentLogger::class.java
            )
            return FluentLogger(Platform.getBackend(loggingClass))
        }
    }
}
