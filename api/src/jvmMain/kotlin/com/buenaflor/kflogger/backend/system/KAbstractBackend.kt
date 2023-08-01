package com.buenaflor.kflogger.backend.system

// Suppress errors: protected Java visibility
@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
public actual typealias KAbstractBackend = AbstractBackend

public actual val KAbstractBackend.loggerName: String
  get() = loggerName
