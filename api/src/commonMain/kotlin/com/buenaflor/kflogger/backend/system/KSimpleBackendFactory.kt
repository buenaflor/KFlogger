package com.buenaflor.kflogger.backend.system

import com.buenaflor.kflogger.backend.KLoggerBackend

/**
 * Default factory for creating logger backends.
 *
 *
 * See class documentation in [BackendFactory] for important implementation restrictions.
 */
public expect class KSimpleBackendFactory private constructor() : KBackendFactory {
    public override fun create(loggingClassName: String): KLoggerBackend

    override fun toString(): String

    public companion object {
        public fun getInstance(): KBackendFactory
    }
}
