package com.buenaflor.kflogger

@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
actual typealias KLogSite = LogSite

actual val KLogSite.className: String?
  get() = className

actual val KLogSite.methodName: String?
  get() = methodName

actual val KLogSite.lineNumber: Int
  get() = lineNumber

actual val KLogSite.fileName: String?
  get() = fileName
