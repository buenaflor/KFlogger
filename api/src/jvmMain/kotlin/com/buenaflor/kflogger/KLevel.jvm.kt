package com.buenaflor.kflogger

@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
public actual typealias KLevel = java.util.logging.Level

public actual fun KLevel.intValue(): Int {
    return this.intValue()
}