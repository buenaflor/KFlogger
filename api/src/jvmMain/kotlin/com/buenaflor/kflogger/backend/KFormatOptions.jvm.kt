package com.buenaflor.kflogger.backend

// Suppress errors: companion object - Java static does not match companion object
@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
public actual typealias KFormatOptions = FormatOptions

public actual fun KFormatOptions.precision(): Int = precision
