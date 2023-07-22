package com.buenaflor.kflogger

import com.buenaflor.kflogger.backend.KLoggerBackend

@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
public actual typealias KAbstractLogger<API> = AbstractLogger<API>

public actual val <API : KLoggingApi<API>> KAbstractLogger<API>.backend: KLoggerBackend
  get() = backend
