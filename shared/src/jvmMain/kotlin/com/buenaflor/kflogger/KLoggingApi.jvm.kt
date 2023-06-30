package com.buenaflor.kflogger;

actual typealias KLoggingApi<API> = LoggingApi<API>

actual val <API : KLoggingApi<API>> KAbstractLogger<API>.backend: KLoggerBackend get() = backend
