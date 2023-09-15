package com.giancarlobuenaflor.kflogger

/**
 * Static utility methods for lazy argument evaluation in Flogger. The [.lazy]
 * method allows lambda expressions to be "cast" to the [LazyArg] interface.
 *
 *
 * In cases where the log statement is strongly expected to always be enabled (e.g. unconditional
 * logging at warning or above) it may not be worth using lazy evaluation because any work required
 * to evaluate arguments will happen anyway.
 *
 *
 * If lambdas are available, users should prefer using this class rather than explicitly creating
 * `LazyArg` instances.
 */
// TODO: Add other generally useful methods here, especially things which help non-lambda users.
public expect class KLazyArgs {
  public companion object {
    /**
     * Coerces a lambda expression or method reference to return a lazily evaluated logging argument.
     * Pass in a compatible, no-argument, lambda expression or method reference to have it evaluated
     * only when logging will actually occur.
     *
     * <pre>`logger.atFine().log("value=%s", lazy(() -> doExpensive()));
     * logger.atWarning().atMostEvery(5, MINUTES).log("value=%s", lazy(stats::create));
    `</pre> *
     *
     * Evaluation of lazy arguments occurs at most once, and always in the same thread from which the
     * logging call was made.
     *
     *
     * Note also that it is almost never suitable to make a `toString()` call "lazy" using
     * this mechanism and, in general, explicitly calling `toString()` on arguments which are
     * being logged is an error as it precludes the ability to log an argument structurally.
     */
    public fun <T> lazy(lambdaOrMethodReference: KLazyArg<T>): KLazyArg<T>
  }
}

