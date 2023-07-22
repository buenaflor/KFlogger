package com.buenaflor.kflogger

@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
public actual typealias KLogSite = LogSite

public actual val KLogSite.className: String?
    get() = className

public actual val KLogSite.methodName: String?
    get() = methodName

public actual val KLogSite.lineNumber: Int
    get() = lineNumber

public actual val KLogSite.fileName: String?
    get() = fileName