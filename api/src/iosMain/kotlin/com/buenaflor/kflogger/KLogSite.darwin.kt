package com.buenaflor.kflogger

public actual abstract class KLogSite : KLogSiteKey {
  /** Returns the name of the class containing the log statement. */
  public actual abstract val className: String?

  /** Returns the name of the method containing the log statement. */
  public actual abstract val methodName: String?

  /**
   * Returns a valid line number for the log statement in the range 1 - 65535, or [.UNKNOWN_LINE] if
   * not known.
   *
   * There is a limit of 16 bits for line numbers in a class. See
   * [here](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.12) for more
   * details.
   */
  public actual abstract val lineNumber: Int
  public actual abstract val fileName: String?

  public actual companion object {
    /** A value used for line numbers when the true information is not available. */
    public actual val UNKNOWN_LINE: Int
      get() = TODO("Not yet implemented")

    /**
     * An singleton LogSite instance used to indicate that valid log site information cannot be
     * determined. This can be used to indicate that log site information is not available by
     * injecting it via [LoggingApi.withInjectedLogSite] which will suppress any further log site
     * analysis for that log statement. This is also returned if stack trace analysis fails for any
     * reason.
     *
     * If a log statement does end up with invalid log site information, then any fluent logging
     * methods which rely on being able to look up site specific metadata will be disabled and
     * essentially become "no ops".
     */
    public actual val INVALID: KLogSite
      get() = TODO("Not yet implemented")

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
    public actual fun injectedLogSite(
        internalClassName: String,
        methodName: String,
        encodedLineNumber: Int,
        sourceFileName: String
    ): KLogSite {
      TODO("Not yet implemented")
    }
  }
}
