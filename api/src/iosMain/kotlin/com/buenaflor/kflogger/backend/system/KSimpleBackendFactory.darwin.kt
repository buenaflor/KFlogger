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
        return KSimpleLoggerBackend(KLogger())
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
