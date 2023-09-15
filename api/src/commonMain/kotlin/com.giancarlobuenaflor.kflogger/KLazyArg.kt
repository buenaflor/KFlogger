package com.giancarlobuenaflor.kflogger

/**
 * Functional interface for allowing lazily evaluated arguments to be supplied to Flogger. This
 * allows callers to defer argument evaluation efficiently when:
 *
 * <ul>
 *   <li>Doing "fine" logging that's normally disabled
 *   <li>Applying rate limiting to log statements
 * </ul>
 */
public expect fun interface KLazyArg<T> {
  /**
   * Computes a value to use as a log argument. This method is invoked once the Flogger library has
   * determined that logging will occur, and the returned value is used in place of the `LazyArg` instance that was passed into the log statement.
   */
  public fun evaluate(): T?
}

