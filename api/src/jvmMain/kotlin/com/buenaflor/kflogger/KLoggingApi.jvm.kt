package com.buenaflor.kflogger;

import com.buenaflor.kflogger.backend.KLoggerBackend

actual typealias KLoggingApi<API> = LoggingApi<API>

@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
actual typealias KLoggingApiNoOp<API> = LoggingApi.NoOp<API>

actual val <API : KLoggingApi<API>> KAbstractLogger<API>.backend: KLoggerBackend get() = backend
