package com.buenaflor.kflogger.parameter

// Suppress errors: companion object - Java static does not match companion object
@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
public actual typealias KDateTimeFormat = DateTimeFormat

public actual val KDateTimeFormat.char: Char
  get() = char
