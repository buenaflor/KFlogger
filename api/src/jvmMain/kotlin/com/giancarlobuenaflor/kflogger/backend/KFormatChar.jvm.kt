package com.giancarlobuenaflor.kflogger.backend

// Suppress errors: companion object - Java static does not match companion object
@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
public actual typealias KFormatChar = FormatChar

internal actual val KFormatChar.allowedFlags: Int
  get() = allowedFlags

public actual val KFormatChar.type: KFormatType
  get() = type

public actual val KFormatChar.defaultFormatString: String
  get() = defaultFormatString
