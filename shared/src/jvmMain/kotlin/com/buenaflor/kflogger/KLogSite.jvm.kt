package com.buenaflor.kflogger

@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
actual typealias KLogSite = LogSite

/** Returns the name of the class containing the log statement.  */
actual val KLogSite.className: String? get() = className

/** Returns the name of the method containing the log statement.  */
actual val KLogSite.methodName: String? get() = methodName

/**
 * Returns a valid line number for the log statement in the range 1 - 65535, or
 * [.UNKNOWN_LINE] if not known.
 *
 *
 * There is a limit of 16 bits for line numbers in a class. See
 * [here](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.12)
 * for more details.
 */
actual val KLogSite.lineNumber: Int get() = lineNumber

/**
 * Returns the name of the class file containing the log statement (or null if not known). The
 * source file name is optional and strictly for debugging.
 *
 * <p>Normally this value (if present) is extracted from the SourceFile attribute of the class
 * file (see the <a
 * href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.10">JVM class
 * file format specification</a> for more details).
 */
actual val KLogSite.fileName: String? get() = fileName