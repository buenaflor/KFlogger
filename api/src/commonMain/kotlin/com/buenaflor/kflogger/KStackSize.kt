package com.buenaflor.kflogger

/**
 * Enum values to be passed into [GoogleLoggingApi.withStackTrace] to control the maximum number of
 * stack trace elements created.
 *
 * Note that the precise value returned by [.getMaxDepth] may change over time, but it can be
 * assumed that `SMALL <= MEDIUM <= LARGE <= FULL`.
 */
public expect enum class KStackSize {
  /**
   * Produces a small stack suitable for more fine grained debugging. For performance reasons, this
   * is the only stack size suitable for log statements at level `INFO` or finer, but is may also be
   * useful for `WARNING` level log statements in cases where context is not as important. For
   * `SEVERE` log statements, it is advised to use a stack size of [.MEDIUM] or above.
   *
   * Requesting a small stack trace for log statements which occur under normal circumstances is
   * acceptable, but may affect performance. Consider using [GoogleLoggingApi.withStackTrace] in
   * conjunction with rate limiting methods, such as [LoggingApi.atMostEvery], to mitigate
   * performance issues.
   *
   * The current maximum size of a `SMALL` stack trace is 10 elements, but this may change.
   */
  SMALL,

  /**
   * Produces a medium sized stack suitable for providing contextual information for most log
   * statements at `WARNING` or above. There should be enough stack trace elements in a `MEDIUM`
   * stack to provide sufficient debugging context in most cases.
   *
   * Requesting a medium stack trace for any log statements which can occur regularly under normal
   * circumstances is not recommended.
   *
   * The current maximum size of a `MEDIUM` stack trace is 20 elements, but this may change.
   */
  MEDIUM,

  /**
   * Produces a large stack suitable for providing highly detailed contextual information. This is
   * most useful for `SEVERE` log statements which might be processed by external tools and subject
   * to automated analysis.
   *
   * Requesting a large stack trace for any log statement which can occur under normal circumstances
   * is not recommended.
   *
   * The current maximum size of a `LARGE` stack trace is 50 elements, but this may change.
   */
  LARGE,

  /**
   * Provides the complete stack trace. This is included for situations in which it is known that
   * the upper-most elements of the stack are definitely required for analysis.
   *
   * Requesting a full stack trace for any log statement which can occur under normal circumstances
   * is not recommended.
   */
  FULL,

  /**
   * Provides no stack trace, making the `withStackTrace()` method an effective no-op. This is
   * useful when your stack size is conditional. For example:
   * <pre> `logger.atWarning()
   * .withStackTrace(showTrace ? StackSize.MEDIUM : StackSize.NONE)
   * .log("message");
   * `</pre> *
   */
  NONE
}
