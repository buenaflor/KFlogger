package com.buenaflor.kflogger

import com.buenaflor.kflogger.backend.KMetadataKey

/**
 * The basic logging API. An implementation of this API (or an extension of it) will be
 * returned by any fluent logger, and forms the basis of the fluent call chain.
 *
 *
 * In typical usage each method in the API, with the exception of the terminal `log()`
 * statements, will carry out some simple task (which may involve modifying the context of the log
 * statement) and return the same API for chaining. The exceptions to this are:
 *
 *  * Methods which return a NoOp implementation of the API in order to disable logging.
 *  * Methods which return an alternate API in order to implement context specific grammar (though
 * these alternate APIs should always return the original logging API eventually).
 *
 * A hypothetical example of a context specific grammar might be:
 * <pre>`logger.at(WARNING).whenSystem().isLowOnMemory().log("");
`</pre> *
 * In this example the `whenSystem()` method would return its own API with several context
 * specific methods (`isLowOnMemory()`, `isThrashing()` etc...), however each of these
 * sub-APIs must eventually return the original logging API.
 */
// NOTE: new methods to this interface should be coordinated with google-java-format
expect interface KLoggingApi<API : KLoggingApi<API>> {
    /**
     * Associates a [Throwable] instance with the current log statement, to be interpreted as
     * the cause of this statement. Typically this method will be used from within catch blocks to log
     * the caught exception or error. If the cause is `null` then this method has no effect.
     *
     *
     * If this method is called multiple times for a single log statement, the last invocation will
     * take precedence.
     */
    fun withCause(cause: Throwable?): API

    /**
     * Modifies the current log statement to be emitted at most one-in-N times. The specified count
     * must be greater than zero and it is expected, but not required, that it is constant. In the
     * absence of any other rate limiting, this method always allows the first invocation of any log
     * statement to be emitted.
     *
     * <h3>Notes</h3>
     *
     * If *multiple rate limiters* are used for a single log statement, that log statement will
     * only be emitted once all rate limiters have reached their threshold, and when a log statement
     * is emitted all the rate limiters are reset. In particular for `every(N)` this means that
     * logs need not always be emitted at multiples of `N` if other rate limiters are active,
     * though it will always be at least `N`.
     *
     *
     * When rate limiting is active, a `"skipped"` count is added to log statements to indicate
     * how many logs were disallowed since the last log statement was emitted.
     *
     *
     * If this method is called multiple times for a single log statement, the last invocation will
     * take precedence.
     *
     * @param n the factor by which to reduce logging frequency.
     * @throws IllegalArgumentException if `n` is not positive.
     */
    fun every(n: Int): API

    /**
     * Modifies the current log statement to be emitted with likelihood 1 in `n`. For example,
     * inserting `onAverageEvery(20)` into a call chain results in approximately 5% as many
     * messages being emitted as before. Unlike the other rate-limiting options, there is no
     * guarantee about when the first such message will be emitted, though it becomes highly likely as
     * the number of calls reaches several times `n`.
     *
     * <h3>Notes</h3>
     *
     * If *multiple rate limiters* are used for a single log statement, that log statement will
     * only be emitted once all rate limiters have reached their threshold, and when a log statement
     * is emitted all the rate limiters are reset. In particular for `onAverageEvery(N)` this
     * means that logs may occurs less frequently than one-in-N if other rate limiters are active.
     *
     *
     * When rate limiting is active, a `"skipped"` count is added to log statements to indicate
     * how many logs were disallowed since the last log statement was emitted.
     *
     *
     * If this method is called multiple times for a single log statement, the last invocation will
     * take precedence.
     *
     * @param n the factor by which to reduce logging frequency; a value of `1` has no effect.
     * @throws IllegalArgumentException if `n` is not positive.
     */
    fun onAverageEvery(n: Int): API
    /**
     * Modifies the current log statement to be emitted at most once per specified time period. The
     * specified duration must not be negative, and it is expected, but not required, that it is
     * constant.  In the absence of any other rate limiting, this method always allows the first
     * invocation of any log statement to be emitted.
     *
     *
     * Note that for performance reasons `atMostEvery()` is explicitly *not* intended to
     * perform "proper" rate limiting to produce a limited average rate over many samples.
     *
     * <h3>Behaviour</h3>
     *
     * A call to `atMostEvery()` will emit the current log statement if:
     * <pre>`currentTimestampNanos >= lastTimestampNanos + unit.toNanos(n)
    `</pre> *
     * where `currentTimestampNanos` is the timestamp of the current log statement and
     * `lastTimestampNanos` is a time stamp of the last log statement that was emitted.
     *
     *
     * The effect of this is that when logging invocation is relatively infrequent, the period
     * between emitted log statements can be higher than the specified duration. For example
     * if the following log statement were called every 600ms:
     * <pre>`logger.atFine().atMostEvery(2, SECONDS).log(...);
    `</pre> *
     * logging would occur after `0s`, `2.4s` and `4.8s` (not `4.2s`),
     * giving an effective duration of `2.4s` between log statements over time.
     *
     *
     * Providing a zero length duration (ie, `n == 0`) disables rate limiting and makes this
     * method an effective no-op.
     *
     * <h3>Granularity</h3>
     *
     * Because the implementation of this feature relies on a nanosecond timestamp provided by the
     * backend, the actual granularity of the underlying clock used may vary, and it is possible to
     * specify a time period smaller than the smallest visible time increment. If this occurs, then
     * the effective rate limit applied to the log statement will be the smallest available time
     * increment. For example, if the system clock granularity is 1 millisecond, and a
     * log statement is called with `atMostEvery(700, MICROSECONDS)`, the effective rate of
     * logging (even averaged over long periods) could never be more than once every millisecond.
     *
     * <h3>Notes</h3>
     *
     * If *multiple rate limiters* are used for a single log statement, that log statement will
     * only be emitted once all rate limiters have reached their threshold, and when a log statement
     * is emitted all the rate limiters are reset. So even if the rate limit duration has expired, it
     * does not mean that logging will occur.
     *
     *
     * When rate limiting is active, a `"skipped"` count is added to log statements to indicate
     * how many logs were disallowed since the last log statement was emitted.
     *
     *
     * If this method is called multiple times for a single log statement, the last invocation will
     * take precedence.
     *
     * @param n the minimum number of time units between emitted log statements
     * @param unit the time unit for the duration
     * @throws IllegalArgumentException if `n` is negative.
     */
    fun atMostEvery(n: Int, unit: KTimeUnit): API

    /**
     * Aggregates stateful logging with respect to a given `key`.
     *
     *
     * Normally log statements with conditional behaviour (e.g. rate limiting) use the same state
     * for each invocation (e.g. counters or timestamps). This method allows an additional qualifier
     * to be given which allows for different conditional state for each unique qualifier.
     *
     *
     * This only makes a difference for log statements which use persistent state to control
     * conditional behaviour (e.g. `atMostEvery()` or `every()`).
     *
     *
     * This is the most general form of log aggregation and allows any keys to be used, but it
     * requires the caller to have chosen a bucketing strategy. Where it is possible to refactor code
     * to avoid passing keys from an unbounded space into the `per(...)` method (e.g. by
     * mapping cases to an `Enum`), this is usually preferable.
     *
     * When using this method, a bucketing strategy is needed to reduce the risk of leaking memory.
     * Consider the alternate API:
     *
     * <pre>`// Rate limit per unique error message ("No such file", "File corrupted" etc.).
     * logger.atWarning().per(error.getMessage()).atMostEvery(30, SECONDS).log(...);
    `</pre> *
     *
     *
     * A method such as the one above would need to store some record of all the unique messages it
     * has seen in order to perform aggregation. This means that the API would suffer a potentially
     * unbounded memory leak if a timestamp were included in the message (since all values would now
     * be unique and need to be retained).
     *
     *
     * To fix (or at least mitigate) this issue, a [LogPerBucketingStrategy] is passed to
     * provide a mapping from "unbounded key space" (e.g. arbitrary strings) to a bounded set of
     * "bucketed" values. In the case of error messages, you might implement a bucketing strategy to
     * classify error messages based on the type of error.
     *
     *
     * This method is most useful in helping to avoid cases where a rare event might never be
     * logged due to rate limiting. For example, the following code will cause log statements with
     * different types of `errorMessage`s to be rate-limited independently of each other.
     *
     * <pre>`// Rate limit for each type of error (FileNotFoundException, CorruptedFileException etc.).
     * logger.atInfo().per(error, byClass()).atMostEvery(30, SECONDS).log(...);
    `</pre> *
     *
     *
     * If a user knows that the given `key` values really do form a strictly bounded set,
     * the [KLogPerBucketingStrategy.knownBounded] strategy can be used, but it should always
     * be documented as to why this is safe.
     *
     *
     * The `key` passed to this method should always be a variable (passing a constant value
     * has no effect). If a `null` key is passed, this call has no effect (e.g. rate limiting
     * will apply normally, without respect to any specific scope).
     *
     *
     * If multiple aggregation keys are added to a single log statement, then they all take effect
     * and logging is aggregated by the unique combination of keys passed to all "per" methods.
     */
    fun <T> per(key: T?, strategy: KLogPerBucketingStrategy<in T>?): API

    /**
     * Aggregates stateful logging with respect to the given enum value.
     *
     *
     * Normally log statements with conditional behaviour (e.g. rate limiting) use the same state
     * for each invocation (e.g. counters or timestamps). This method allows an additional qualifier
     * to be given which allows for different conditional state for each unique qualifier.
     *
     *
     * This only makes a difference for log statements which use persistent state to control
     * conditional behaviour (e.g. `atMostEvery()` or `every()`).
     *
     *
     * This method is most useful in helping to avoid cases where a rare event might never be
     * logged due to rate limiting. For example, the following code will cause log statements with
     * different `taskType`s to be rate-limited independently of each other.
     *
     * <pre>`// We want to rate limit logging separately for all task types.
     * logger.at(INFO).per(taskType).atMostEvery(30, SECONDS).log("Start task: %s", taskSpec);
    `</pre> *
     *
     *
     * The `key` passed to this method should always be a variable (passing a constant value
     * has no effect). If `null` is passed, this call has no effect (e.g. rate limiting will
     * apply normally, without respect to any specific scope).
     *
     *
     * If multiple aggregation keys are added to a single log statement, then they all take effect
     * and logging is aggregated by the unique combination of keys passed to all "per" methods.
     */
    fun per(key: Enum<*>?): API

    /**
     * Aggregates stateful logging with respect to a scoped context determined by the given scope
     * provider.
     *
     *
     * When [ScopedLoggingContext][com.buenaflor.kflogger.context.ScopedLoggingContext] is
     * used to create a context, it can be bound to a [ ]. For example:
     *
     * <pre>`ScopedLoggingContexts.newContext(REQUEST).run(() -> scopedMethod(x, y, z));
    `</pre> *
     *
     * where [REQUEST][com.buenaflor.kflogger.context.ScopeType.REQUEST] defines the scope
     * type for the context in which `scopedMethod()` is called. Within this context, the scope
     * associated with the `REQUEST` type can then be used to aggregate logging behavior:
     *
     * <pre>`logger.atInfo().atMostEvery(5, SECONDS).per(REQUEST).log("Some message...");
    `</pre> *
     *
     *
     * New scope types can be created for specific subtasks using [ ][com.buenaflor.kflogger.context.ScopeType.create] but it is
     * recommended to use shared constants (such as `ScopeType.REQUEST`) wherever feasible to
     * avoid confusion.
     *
     *
     * Note that in order for the request scope to be applied to a log statement, the `per(REQUEST)` method must still be called; just being inside the request scope isn't enough.
     *
     *
     * Unlike other `per()` methods, this method is expected to be given a constant value.
     * This is because the given value *provides* the current scope, rather than *being*
     * the current scope.
     *
     *
     * If a log statement using this method is invoked outside a context of the given type, this
     * call has no effect (e.g. rate limiting will apply normally, without respect to any specific
     * scope).
     *
     *
     * If multiple aggregation keys are added to a single log statement, then they all take effect
     * and logging is aggregated by the unique combination of keys passed to all "per" methods.
     *
     * @param scopeProvider a constant used to defined the type of the scope in which logging is
     * aggregated.
     */
    fun per(scopeProvider: KLoggingScopeProvider?): API

    /**
     * Creates a synthetic exception and attaches it as the "cause" of the log statement as a way to
     * provide additional context for the logging call itself. The exception created by this method is
     * always of the type [LogSiteStackTrace], and its message indicates the stack size.
     *
     *
     * If the `withCause(e)` method is also called for the log statement (either before or
     * after) `withStackTrace()`, the given exception becomes the cause of the synthetic
     * exception.
     *
     * @param size the maximum size of the stack trace to be generated.
     */
    fun withStackTrace(size: KStackSize?): API

    /**
     * Associates a metadata key constant with a runtime value for this log statement in a structured
     * way that is accessible to logger backends.
     *
     *
     * This method is not a replacement for general parameter passing in the [.log] method
     * and should be reserved for keys/values with specific semantics. Examples include:
     *
     *  * Keys that are recognised by specific logger backends (typically to control logging
     * behaviour in some way).
     *  * Key value pairs which are explicitly extracted from logs by tools.
     *
     *
     *
     * Metadata keys can support repeated values (see [KMetadataKey.canRepeat]), and if a
     * repeatable key is used multiple times in the same log statement, the effect is to collect all
     * the given values in order. If a non-repeatable key is passed multiple times, only the last
     * value is retained (though callers should not rely on this behavior and should simply avoid
     * repeating non-repeatable keys).
     *
     *
     * If `value` is `null`, this method is a no-op. This is useful for specifying
     * conditional values (e.g. via `logger.atInfo().with(MY_KEY, getValueOrNull()).log(...)`).
     *
     * @param key the metadata key (expected to be a static constant)
     * @param value a value to be associated with the key in this log statement. Null values are
     * allowed, but the effect is always a no-op
     * @throws NullPointerException if the given key is null
     * @see KMetadataKey
     */
    fun <T> with(key: KMetadataKey<T>, value: T?): API

    /**
     * Sets a boolean metadata key constant to `true` for this log statement in a structured way
     * that is accessible to logger backends.
     *
     *
     * This method is not a replacement for general parameter passing in the [.log] method
     * and should be reserved for keys/values with specific semantics. Examples include:
     *
     *
     *  * Keys that are recognised by specific logger backends (typically to control logging
     * behaviour in some way).
     *  * Key value pairs which are explicitly extracted from logs by tools.
     *
     *
     *
     * This method is just an alias for `with(key, true)` to improve readability.
     *
     * @param key the boolean metadata key (expected to be a static constant)
     * @throws NullPointerException if the given key is null
     * @see KMetadataKey
     */
    fun with(key: KMetadataKey<Boolean>?): API

    /**
     * Sets the log site for the current log statement. Explicit log site injection is very rarely
     * necessary, since either the log site is injected automatically, or it is determined at runtime
     * via stack analysis. The one use case where calling this method explicitly may be useful is when
     * making logging helper methods, where some common project specific logging behavior is
     * enshrined. For example, you can write:
     *
     * <pre>`public void logStandardWarningAt(LogSite logSite, String message, Object... args) {
     * logger.atWarning()
     * .withInjectedLogSite(logSite)
     * .atMostEvery(5, MINUTES)
     * .logVarargs(message, args);
     * }
    `</pre> *
     *
     * and then code can do:
     *
     * <pre>`import static com.buenaflor.kflogger.LogSites.logSite;
    `</pre> *
     *
     * and elsewhere:
     *
     * <pre>`logStandardWarningAt(logSite(), "Badness");
     * ...
     * logStandardWarningAt(logSite(), "More badness: %s", getData());
    `</pre> *
     *
     *
     * Now each of the call sites for the helper method is treated as if it were in the logging
     * API, and things like rate limiting work separately for each, and the location in the log
     * statement will be the point at which the helper method was called.
     *
     *
     * It is very important to note that the `logSite()` call can be very slow, since
     * determining the log site can involve stack trace analysis. It is only recommended in cases
     * where logging is expected to occur (e.g. `WARNING` level or above). Luckily, there is
     * typically no need to implement helper methods for `FINE` logging, since it's usually less
     * structured and doesn't normally need to follow any specific "best practice" behavior.
     *
     *
     * Note however that any stack traces generated by [.withStackTrace] will
     * still contain the complete stack, including the call to the logger itself inside the helper
     * method.
     *
     *
     * This method must only be explicitly called once for any log statement, and if this method is
     * called multiple times the first invocation will take precedence. This is because log site
     * injection (if present) is expected to occur just before the final `log()` call and must
     * be overrideable by earlier (explicit) calls. A null argument has no effect.
     *
     * @param logSite Log site which uniquely identifies any per-log statement resources.
     */
    fun withInjectedLogSite(logSite: KLogSite?): API

    /**
     * Internal method not for public use. This method is only intended for use by the logger
     * agent and related classes and should never be invoked manually.
     *
     * @param internalClassName Slash separated class name obtained from the class constant pool.
     * @param methodName Method name obtained from the class constant pool.
     * @param encodedLineNumber line number and per-line log statement index encoded as a single
     * 32-bit value. The low 16-bits is the line number (0 to 0xFFFF inclusive) and the high
     * 16 bits is a log statement index to distinguish multiple statements on the same line
     * (this becomes important if line numbers are stripped from the class file and everything
     * appears to be on the same line).
     * @param sourceFileName Optional base name of the source file (this value is strictly for
     * debugging and does not contribute to either equals() or hashCode() behavior).
     */
    fun withInjectedLogSite(
        internalClassName: String?,
        methodName: String?,
        encodedLineNumber: Int,
        sourceFileName: String?
    ): API

    /**
     * Returns true if logging is enabled at the level implied for this API, according to the current
     * logger backend. For example:
     * <pre>`if (logger.atFine().isEnabled()) {
     * // Do non-trivial argument processing
     * logger.atFine().log("Message: %s", value);
     * }
    `</pre> *
     *
     *
     * Note that if logging is enabled for a log level, it does not always follow that the log
     * statement will definitely be written to the backend (due to the effects of other methods in
     * the fluent chain), but if this method returns `false` then it can safely be assumed that
     * no logging will occur.
     *
     *
     * This method is unaffected by additional methods in the fluent chain and should only ever be
     * invoked immediately after the level selector method. In other words, the expression:
     * <pre>`logger.atFine().every(100).isEnabled()`</pre>
     * is incorrect because it will always behave identically to:
     * <pre>`logger.atFine().isEnabled()`</pre>
     *
     *
     * <h3>Implementation Note</h3>
     * By avoiding passing a separate `Level` at runtime to determine "loggability", this API
     * makes it easier to coerce bytecode optimizers into doing "dead code" removal on sections
     * guarded by this method.
     *
     *
     * If a proxy logger class is supplied for which:
     * <pre>`logger.atFine()`</pre>
     * unconditionally returns the "NoOp" implementation of the API (in which `isEnabled()`
     * always returns `false`), it becomes simple for bytecode analysis to determine that:
     * <pre>`logger.atFine().isEnabled()`</pre>
     * always evaluates to `false` .
     */
    fun isEnabled(): Boolean

    /**
     * Logs a formatted representation of values in the given array, using the specified message
     * template.
     *
     *
     * This method is only expected to be invoked with an existing varargs array passed in from
     * another method. Unlike [.log], which would treat an array as a single
     * parameter, this method will unwrap the given array.
     *
     * @param message the message template string containing an argument placeholder for each element
     * of `varargs`.
     * @param varargs the non-null array of arguments to be formatted.
     */
    fun logVarargs(message: String?, varargs: Array<Any?>?)

    /**
     * Terminal log statement when a message is not required. A `log` method must terminate all
     * fluent logging chains and the no-argument method can be used if there is no need for a log
     * message. For example:
     * <pre>`logger.at(INFO).withCause(error).log();
    `</pre> *
     *
     *
     * However as it is good practice to give all log statements a meaningful log message, use of this
     * method should be rare.
     */
    fun log()

    /**
     * Logs the given literal string without interpreting any argument placeholders.
     *
     *
     * Important: This is intended only for use with hard-coded, literal strings which cannot
     * contain user data. If you wish to log user generated data, you should do something like:
     * <pre>`log("user data=%s", value);
    `</pre> *
     * This serves to give the user data context in the log file but, more importantly, makes it
     * clear which arguments may contain PII and other sensitive data (which might need to be
     * scrubbed during logging). This recommendation also applies to all the overloaded `log()`
     * methods below.
     */
    fun log(msg: String?)

    // ---- Overloads for object arguments (to avoid vararg array creation). ----

    /**
     * Logs a formatted representation of the given parameter, using the specified message template.
     * The message string is expected to contain argument placeholder terms appropriate to the
     * logger's choice of parser.
     *
     *
     * Note that printf-style loggers are always expected to accept the standard Java printf
     * formatting characters (e.g. "%s", "%d" etc...) and all flags unless otherwise stated.
     * Null arguments are formatted as the literal string `"null"` regardless of
     * formatting flags.
     *
     * @param msg the message template string containing a single argument placeholder.
     */
    fun log(msg: String?, p1: Any?)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Any?, p2: Any?)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Any?, p2: Any?, p3: Any?)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(
        msg: String?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    )

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(
        msg: String?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    )

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(
        msg: String?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    )

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(
        msg: String?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    )

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(
        msg: String?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    )

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(
        msg: String?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    )

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(
        msg: String?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?,
        p10: Any?
    )

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(
        msg: String?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?,
        p10: Any?,
        vararg rest: Any?
    )

    // ---- Overloads for a single argument (to avoid auto-boxing and vararg array creation). ----

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Char)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Byte)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Short)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Int)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Long)

    // ---- Overloads for two arguments (to avoid auto-boxing and vararg array creation). ----

    /*
   * It may not be obvious why we need _all_ combinations of fundamental types here (because some
   * combinations should be rare enough that we can ignore them). However due to the precedence in
   * the Java compiler for converting fundamental types in preference to auto-boxing, and the need
   * to preserve information about the original type (byte, short, char etc...) when doing unsigned
   * formatting, it turns out that all combinations are required.
   */
    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Any?, p2: Boolean)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Any?, p2: Char)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Any?, p2: Byte)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Any?, p2: Short)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Any?, p2: Int)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Any?, p2: Long)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Any?, p2: Float)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Any?, p2: Double)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Boolean, p2: Any?)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Char, p2: Any?)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Byte, p2: Any?)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Short, p2: Any?)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Int, p2: Any?)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Long, p2: Any?)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Float, p2: Any?)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Double, p2: Any?)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Boolean, p2: Boolean)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Char, p2: Boolean)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Byte, p2: Boolean)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Short, p2: Boolean)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Int, p2: Boolean)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Long, p2: Boolean)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Float, p2: Boolean)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Double, p2: Boolean)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Boolean, p2: Char)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Char, p2: Char)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Byte, p2: Char)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Short, p2: Char)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Int, p2: Char)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Long, p2: Char)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Float, p2: Char)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Double, p2: Char)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Boolean, p2: Byte)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Char, p2: Byte)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Byte, p2: Byte)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Short, p2: Byte)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Int, p2: Byte)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Long, p2: Byte)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Float, p2: Byte)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Double, p2: Byte)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Boolean, p2: Short)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Char, p2: Short)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Byte, p2: Short)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Short, p2: Short)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Int, p2: Short)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Long, p2: Short)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Float, p2: Short)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Double, p2: Short)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Boolean, p2: Int)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Char, p2: Int)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Byte, p2: Int)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Short, p2: Int)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Int, p2: Int)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Long, p2: Int)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Float, p2: Int)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Double, p2: Int)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Boolean, p2: Long)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Char, p2: Long)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Byte, p2: Long)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Short, p2: Long)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Int, p2: Long)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Long, p2: Long)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Float, p2: Long)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Double, p2: Long)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Boolean, p2: Float)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Char, p2: Float)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Byte, p2: Float)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Short, p2: Float)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Int, p2: Float)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Long, p2: Float)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Float, p2: Float)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Double, p2: Float)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Boolean, p2: Double)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Char, p2: Double)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Byte, p2: Double)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Short, p2: Double)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Int, p2: Double)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Long, p2: Double)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Float, p2: Double)

    /** Logs a message with formatted arguments (see [.log] for details).  */
    fun log(msg: String?, p1: Double, p2: Double)
}

/**
 * An implementation of {@link KLoggingApi} which does nothing and discards all parameters.
 * <p>
 * This class (or a subclass in the case of an extended API) should be returned whenever logging
 * is definitely disabled (e.g. when the log level is too low).
 */
expect open class KLoggingNoOp<API : KLoggingApi<API>> : KLoggingApi<API> {

    protected fun noOp(): API

    final override fun withCause(cause: Throwable?): API

    final override fun every(n: Int): API

    final override fun onAverageEvery(n: Int): API

    final override fun atMostEvery(n: Int, unit: KTimeUnit): API

    final override fun <T> per(key: T?, strategy: KLogPerBucketingStrategy<in T>?): API

    final override fun per(key: Enum<*>?): API

    final override fun per(scopeProvider: KLoggingScopeProvider?): API

    final override fun withStackTrace(size: KStackSize?): API

    final override fun <T> with(key: KMetadataKey<T>, value: T?): API

    final override fun with(key: KMetadataKey<Boolean>?): API

    final override fun withInjectedLogSite(logSite: KLogSite?): API

    final override fun withInjectedLogSite(
        internalClassName: String?,
        methodName: String?,
        encodedLineNumber: Int,
        sourceFileName: String?
    ): API

    final override fun isEnabled(): Boolean

    final override fun logVarargs(message: String?, varargs: Array<Any?>?)

    final override fun log()

    final override fun log(msg: String?)

    // ---- Overloads for object arguments (to avoid vararg array creation). ----

    final override fun log(msg: String?, p1: Any?)

    final override fun log(msg: String?, p1: Any?, p2: Any?)

    final override fun log(msg: String?, p1: Any?, p2: Any?, p3: Any?)

    final override fun log(
        msg: String?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    )

    final override fun log(
        msg: String?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    )

    final override fun log(
        msg: String?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    )

    final override fun log(
        msg: String?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    )

    final override fun log(
        msg: String?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    )

    final override fun log(
        msg: String?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    )

    final override fun log(
        msg: String?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?,
        p10: Any?
    )

    final override fun log(
        msg: String?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?,
        p10: Any?,
        vararg rest: Any?
    )

    // ---- Overloads for a single argument (to avoid auto-boxing and vararg array creation). ----

    final override fun log(msg: String?, p1: Char)

    final override fun log(msg: String?, p1: Byte)

    final override fun log(msg: String?, p1: Short)

    final override fun log(msg: String?, p1: Int)

    final override fun log(msg: String?, p1: Long)

    // ---- Overloads for two arguments (to avoid auto-boxing and vararg array creation). ----

    /*
   * It may not be obvious why we need _all_ combinations of final override fundamental types here (because some
   * combinations should be rare enough that we can ignore them). However due to the precedence in
   * the Java compiler for converting final override fundamental types in preference to auto-boxing, and the need
   * to preserve information about the original type (byte, short, char etc...) when doing unsigned
   * formatting, it turns out that all combinations are required.
   */
    final override fun log(msg: String?, p1: Any?, p2: Boolean)

    final override fun log(msg: String?, p1: Any?, p2: Char)

    final override fun log(msg: String?, p1: Any?, p2: Byte)

    final override fun log(msg: String?, p1: Any?, p2: Short)

    final override fun log(msg: String?, p1: Any?, p2: Int)

    final override fun log(msg: String?, p1: Any?, p2: Long)

    final override fun log(msg: String?, p1: Any?, p2: Float)

    final override fun log(msg: String?, p1: Any?, p2: Double)

    final override fun log(msg: String?, p1: Boolean, p2: Any?)

    final override fun log(msg: String?, p1: Char, p2: Any?)

    final override fun log(msg: String?, p1: Byte, p2: Any?)

    final override fun log(msg: String?, p1: Short, p2: Any?)

    final override fun log(msg: String?, p1: Int, p2: Any?)

    final override fun log(msg: String?, p1: Long, p2: Any?)

    final override fun log(msg: String?, p1: Float, p2: Any?)

    final override fun log(msg: String?, p1: Double, p2: Any?)

    final override fun log(msg: String?, p1: Boolean, p2: Boolean)

    final override fun log(msg: String?, p1: Char, p2: Boolean)

    final override fun log(msg: String?, p1: Byte, p2: Boolean)

    final override fun log(msg: String?, p1: Short, p2: Boolean)

    final override fun log(msg: String?, p1: Int, p2: Boolean)

    final override fun log(msg: String?, p1: Long, p2: Boolean)

    final override fun log(msg: String?, p1: Float, p2: Boolean)

    final override fun log(msg: String?, p1: Double, p2: Boolean)

    final override fun log(msg: String?, p1: Boolean, p2: Char)

    final override fun log(msg: String?, p1: Char, p2: Char)

    final override fun log(msg: String?, p1: Byte, p2: Char)

    final override fun log(msg: String?, p1: Short, p2: Char)

    final override fun log(msg: String?, p1: Int, p2: Char)

    final override fun log(msg: String?, p1: Long, p2: Char)

    final override fun log(msg: String?, p1: Float, p2: Char)

    final override fun log(msg: String?, p1: Double, p2: Char)

    final override fun log(msg: String?, p1: Boolean, p2: Byte)

    final override fun log(msg: String?, p1: Char, p2: Byte)

    final override fun log(msg: String?, p1: Byte, p2: Byte)

    final override fun log(msg: String?, p1: Short, p2: Byte)

    final override fun log(msg: String?, p1: Int, p2: Byte)

    final override fun log(msg: String?, p1: Long, p2: Byte)

    final override fun log(msg: String?, p1: Float, p2: Byte)

    final override fun log(msg: String?, p1: Double, p2: Byte)

    final override fun log(msg: String?, p1: Boolean, p2: Short)

    final override fun log(msg: String?, p1: Char, p2: Short)

    final override fun log(msg: String?, p1: Byte, p2: Short)

    final override fun log(msg: String?, p1: Short, p2: Short)

    final override fun log(msg: String?, p1: Int, p2: Short)

    final override fun log(msg: String?, p1: Long, p2: Short)

    final override fun log(msg: String?, p1: Float, p2: Short)

    final override fun log(msg: String?, p1: Double, p2: Short)

    final override fun log(msg: String?, p1: Boolean, p2: Int)

    final override fun log(msg: String?, p1: Char, p2: Int)

    final override fun log(msg: String?, p1: Byte, p2: Int)

    final override fun log(msg: String?, p1: Short, p2: Int)

    final override fun log(msg: String?, p1: Int, p2: Int)

    final override fun log(msg: String?, p1: Long, p2: Int)

    final override fun log(msg: String?, p1: Float, p2: Int)

    final override fun log(msg: String?, p1: Double, p2: Int)

    final override fun log(msg: String?, p1: Boolean, p2: Long)

    final override fun log(msg: String?, p1: Char, p2: Long)

    final override fun log(msg: String?, p1: Byte, p2: Long)

    final override fun log(msg: String?, p1: Short, p2: Long)

    final override fun log(msg: String?, p1: Int, p2: Long)

    final override fun log(msg: String?, p1: Long, p2: Long)

    final override fun log(msg: String?, p1: Float, p2: Long)

    final override fun log(msg: String?, p1: Double, p2: Long)

    final override fun log(msg: String?, p1: Boolean, p2: Float)

    final override fun log(msg: String?, p1: Char, p2: Float)

    final override fun log(msg: String?, p1: Byte, p2: Float)

    final override fun log(msg: String?, p1: Short, p2: Float)

    final override fun log(msg: String?, p1: Int, p2: Float)

    final override fun log(msg: String?, p1: Long, p2: Float)

    final override fun log(msg: String?, p1: Float, p2: Float)

    final override fun log(msg: String?, p1: Double, p2: Float)

    final override fun log(msg: String?, p1: Boolean, p2: Double)

    final override fun log(msg: String?, p1: Char, p2: Double)

    final override fun log(msg: String?, p1: Byte, p2: Double)

    final override fun log(msg: String?, p1: Short, p2: Double)

    final override fun log(msg: String?, p1: Int, p2: Double)

    final override fun log(msg: String?, p1: Long, p2: Double)

    final override fun log(msg: String?, p1: Float, p2: Double)

    final override fun log(msg: String?, p1: Double, p2: Double)
}

