package com.buenaflor.kflogger

actual typealias KLogData = com.buenaflor.kflogger.backend.LogData

actual val KLogData.level: KLevel? get() = level