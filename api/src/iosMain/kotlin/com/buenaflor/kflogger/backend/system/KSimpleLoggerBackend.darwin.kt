package com.buenaflor.kflogger.backend.system

import com.buenaflor.kflogger.KLogger
import com.buenaflor.kflogger.backend.KLogData
import com.buenaflor.kflogger.backend.message
import platform.Foundation.NSLog

/** A logging backend that uses the `NSLog` class to output log statements.  */
public actual class KSimpleLoggerBackend actual constructor(logger: KLogger) : KAbstractBackend(logger) {
    public actual override fun log(data: KLogData?) {
        data?.getTemplateContext()?.message?.let { NSLog(it) }
    }

    public actual override fun handleError(error: RuntimeException, badData: KLogData) {

    }
}
