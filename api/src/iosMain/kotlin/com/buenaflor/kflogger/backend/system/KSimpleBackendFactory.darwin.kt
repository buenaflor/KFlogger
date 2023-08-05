package com.buenaflor.kflogger.backend.system

import com.buenaflor.kflogger.KLogger
import com.buenaflor.kflogger.backend.KLoggerBackend

/**
 * Default factory for creating logger backends.
 *
 *
 * See class documentation in [BackendFactory] for important implementation restrictions.
 */
public actual class KSimpleBackendFactory private actual constructor() : KBackendFactory() {
    public actual override fun create(loggingClassName: String): KLoggerBackend {
        // TODO KFlogger
        val indexOfFirstUppercase = loggingClassName.indexOfFirst { it.isUpperCase() }
        val subsystem = if (indexOfFirstUppercase != -1) {
            loggingClassName.substring(0, indexOfFirstUppercase - 1)
        } else {
            loggingClassName
        }
        val loggerName = loggingClassName.substring(indexOfFirstUppercase, loggingClassName.length)
        val appender = OSLogSubsystemAppender(subsystem)
        val logger = appender.logger(loggerName)
        return KSimpleLoggerBackend(KLogger(logger))
    }

    actual override fun toString(): String {
        return "Default logger backend factory"
    }

    public actual companion object {
        public actual fun getInstance(): KBackendFactory {
            return KSimpleBackendFactory()
        }
    }
}
