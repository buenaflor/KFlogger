package com.buenaflor.kflogger

/**
 * A value type which representing the location of a single log statement. This class is similar to
 * the `StackTraceElement` class but differs in one important respect.
 *
 * A KLogSite can be associated with a globally unique ID, which can identify a log statement more
 * uniquely than a line number (it is possible to have multiple log statements appear to be on a
 * single line, especially for obfuscated classes).
 *
 * Log sites are intended to be injected into code automatically, typically via some form of
 * bytecode rewriting. Each injection mechanism can have its own implementation of `KLogSite`
 * adapted to its needs.
 *
 * As a fallback, for cases where no injection mechanism is configured, a log site based upon stack
 * trace analysis is used. However due to limitations in the information available from
 * `StackTraceElement`, this log site will not be unique if multiple log statements are on the the
 * same, or if line number information was stripped from the class file.
 */
public expect abstract class KLogSite : KLogSiteKey {
  // Provide a common toString() implementation for only the public attributes.
  public override final fun toString(): String

  public companion object {
    /** A value used for line numbers when the true information is not available. */
    public val UNKNOWN_LINE: Int

    /**
     * An singleton LogSite instance used to indicate that valid log site information cannot be
     * determined. This can be used to indicate that log site information is not available by
     * injecting it via [KLoggingApi.withInjectedLogSite] which will suppress any further log site
     * analysis for that log statement. This is also returned if stack trace analysis fails for any
     * reason.
     *
     * If a log statement does end up with invalid log site information, then any fluent logging
     * methods which rely on being able to look up site specific metadata will be disabled and
     * essentially become "no ops".
     */
    public val INVALID: KLogSite

    /**
     * Creates a log site injected from constants held a class' constant pool.
     *
     * Used for compile-time log site injection, and by the agent.
     *
     * @param internalClassName Slash separated class name obtained from the class constant pool.
     * @param methodName Method name obtained from the class constant pool.
     * @param encodedLineNumber line number and per-line log statement index encoded as a single
     *   32-bit value. The low 16-bits is the line number (0 to 0xFFFF inclusive) and the high 16
     *   bits is a log statement index to distinguish multiple statements on the same line (this
     *   becomes important if line numbers are stripped from the class file and everything appears
     *   to be on the same line).
     * @param sourceFileName Optional base name of the source file (this value is strictly for
     *   debugging and does not contribute to either equals() or hashCode() behavior).
     */
    @Deprecated(
        """this method is only be used for log-site injection and should not be called
    directly.""")
    public fun injectedLogSite(
        internalClassName: String,
        methodName: String,
        encodedLineNumber: Int,
        sourceFileName: String
    ): KLogSite
  }
}

/** Returns the name of the class containing the log statement. */
public expect val KLogSite.className: String?

/** Returns the name of the method containing the log statement. */
public expect val KLogSite.methodName: String?

/**
 * Returns a valid line number for the log statement in the range 1 - 65535, or [.UNKNOWN_LINE] if
 * not known.
 *
 * There is a limit of 16 bits for line numbers in a class. See
 * [here](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.12) for more
 * details.
 */
public expect val KLogSite.lineNumber: Int

/**
 * Returns the name of the class file containing the log statement (or null if not known). The
 * source file name is optional and strictly for debugging.
 *
 * <p>Normally this value (if present) is extracted from the SourceFile attribute of the class file
 * (see the <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.10">JVM
 * class file format specification</a> for more details).
 */
public expect val KLogSite.fileName: String?
