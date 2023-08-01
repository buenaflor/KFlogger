package com.buenaflor.kflogger

import com.buenaflor.kflogger.backend.KLogData
import com.buenaflor.kflogger.backend.KMetadata
import com.buenaflor.kflogger.backend.KTemplateContext
import com.buenaflor.kflogger.parser.KMessageParser

/**
 * The base context for a logging statement, which implements the base logging API.
 *
 * This class is an implementation of the base [LoggingApi] interface and acts as a holder for any
 * state applied to the log statement during the fluent call sequence. The lifecycle of a logging
 * context is very short; it is created by a logger, usually in response to a call to the
 * [AbstractLogger.at] method, and normally lasts only as long as the log statement.
 *
 * This class should not be visible to normal users of the logging API and it is only needed when
 * extending the API to add more functionality. In order to extend the logging API and add methods
 * to the fluent call chain, the `LoggingApi` interface should be extended to add any new methods,
 * and this class should be extended to implement them. A new logger class will then be needed to
 * return the extended context.
 *
 * Logging contexts are not thread safe.
 */
public expect abstract class KLogContext<LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> :
    KLoggingApi<API>, KLogData {

  /**
   * Creates a logging context with the specified level, and with a timestamp obtained from the
   * configured logging [Platform].
   *
   * @param level the log level for this log statement.
   * @param isForced whether to force this log statement (see [.wasForced] for details).
   */
  protected constructor(level: KLevel?, isForced: Boolean)

  /**
   * Creates a logging context with the specified level and timestamp. This constructor is provided
   * only for testing when timestamps need to be injected. In general, subclasses would only need to
   * call this constructor when testing additional API methods which require timestamps (e.g.
   * additional rate limiting functionality). Most unit tests for logger subclasses should not test
   * the value of the timestamp at all, since this is already well tested elsewhere.
   *
   * @param level the log level for this log statement.
   * @param isForced whether to force this log statement (see [.wasForced] for details).
   * @param timestampNanos the nanosecond timestamp for this log statement.
   */
  protected constructor(level: KLevel?, isForced: Boolean, timestampNanos: Long)

  /**
   * Returns the current API (which is just the concrete sub-type of this instance). This is
   * returned by fluent methods to continue the fluent call chain.
   */
  protected abstract fun api(): API

  // ---- Logging Context Constants ----
  /**
   * Returns the logger which created this context. This is implemented as an abstract method to
   * save a field in every context.
   */
  // Cannot be extended as static method since it's abstract
  protected abstract fun getLogger(): LOGGER

  /**
   * Returns the constant no-op logging API, which can be returned by fluent methods in extended
   * logging contexts to efficiently disable logging. This is implemented as an abstract method to
   * save a field in every context.
   */
  protected abstract fun noOp(): API

  /** Returns the msg parser used for all log statements made through this logger. */
  protected abstract fun getMessageParser(): KMessageParser?

  // ---- LogData API ----

  public final override fun wasForced(): Boolean

  // ---- Mutable Metadata ----
  /**
   * Adds the given key/value pair to this logging context. If the key cannot be repeated, and there
   * is already a value for the key in the metadata, then the existing value is replaced, otherwise
   * the value is added at the end of the metadata.
   *
   * @param key the metadata key (see [LogData]).
   * @param value the metadata value.
   */
  protected fun <T> addMetadata(key: KMetadataKey<T>?, value: T)

  /**
   * Removes all key/value pairs with the specified key. Note that this method does not resize any
   * underlying backing arrays or other storage as logging contexts are expected to be short lived.
   *
   * @param key the metadata key (see [LogData]).
   */
  protected fun removeMetadata(key: KMetadataKey<*>?)

  // ---- Post processing ----
  /**
   * A callback which can be overridden to implement post processing of logging contexts prior to
   * passing them to the backend.
   *
   * <h2>Basic Responsibilities</h2>
   *
   * This method is responsible for:
   * 1. Performing any rate limiting operations specific to the extended API.
   * 1. Updating per log-site information (e.g. for debug metrics).
   * 1. Adding any additional metadata to this context.
   * 1. Returning whether logging should be attempted.
   *
   * Implementations of this method must always call `super.postProcess()` first with the given log
   * site key:
   * <pre>`protected boolean postProcess( LogSiteKey logSiteKey) {
   * boolean shouldLog = super.postProcess(logSiteKey);
   * // Handle rate limiting if present.
   * // Add additional metadata etc.
   * return shouldLog;
   * }`</pre>
   *
   * <h2>Log Site Keys</h2>
   *
   * If per log-site information is needed during post-processing, it should be stored using a
   * [LogSiteMap]. This will correctly handle "specialized" log-site keys and remove the risk of
   * memory leaks due to retaining unused log site data indefinitely.
   *
   * Note that the given `logSiteKey` can be more specific than the [LogSite] of a log statement
   * (i.e. a single log statement can have multiple distinct versions of its state). See [.per] for
   * more information.
   *
   * If a log statement cannot be identified uniquely, then `logSiteKey` will be `null`, and this
   * method must behave exactly as if the corresponding fluent method had not been invoked. On a
   * system in which log site information is *unavailable*:
   * <pre>`logger.atInfo().every(100).withCause(e).log("Some msg"); `</pre>
   *
   * should behave exactly the same as:
   * <pre>`logger.atInfo().withCause(e).log("Some msg"); `</pre>
   *
   * <h2>Rate Limiting and Skipped Logs</h2>
   *
   * When handling rate limiting, [.updateRateLimiterStatus] should be called for each active rate
   * limiter. This ensures that even if logging does not occur, the number of "skipped" log
   * statements is recorded correctly and emitted for the next allowed log.
   *
   * If `postProcess()` returns `false` without updating the rate limit status, the log statement
   * may not be counted as skipped. In some situations this is desired, but either way the extended
   * logging API should make it clear to the user (via documentation) what will happen. However in
   * most cases `postProcess()` is only expected to return `false` due to rate limiting.
   *
   * If rate limiters are used there are still situations in which `postProcess()` can return
   * `true`, but logging will not occur. This is due to race conditions around the resetting of rate
   * limiter state. A `postProcess()` method can "early exit" as soon as `shouldLog` is false, but
   * should assume logging will occur while it remains `true`.
   *
   * If a method in the logging chain determines that logging should definitely not occur, it may
   * choose to return the `NoOp` logging API at that point. However this will bypass any
   * post-processing, and no rate limiter state will be updated. This is sometimes desirable, but
   * the API documentation should make it clear to the user as to which behaviour occurs.
   *
   * For example, level selector methods (such as `atInfo()`) return the `NoOp` API for "disabled"
   * log statements, and these have no effect on rate limiter state, and will not update the
   * "skipped" count. This is fine because controlling logging via log level selection is not
   * conceptually a form of "rate limiting".
   *
   * The default implementation of this method enforces the rate limits as set by [ ][.every] and
   * [.atMostEvery].
   *
   * @param logSiteKey used to lookup persistent, per log statement, state.
   * @return true if logging should be attempted (usually based on rate limiter state).
   */
  protected open fun postProcess(logSiteKey: KLogSiteKey?): Boolean

  /**
   * Callback to allow custom log contexts to apply additional rate limiting behaviour. This should
   * be called from within an overriden `postProcess()` method. Typically this is invoked after
   * calling `super.postProcess(logSiteKey)`, such as:
   * <pre>`protected boolean postProcess( LogSiteKey logSiteKey) {
   * boolean shouldLog = super.postProcess(logSiteKey);
   * // Even if `shouldLog` is false, we still call the rate limiter to update its state.
   * shouldLog &= updateRateLimiterStatus(CustomRateLimiter.check(...));
   * if (shouldLog) {
   * // Maybe add additional metadata here...
   * }
   * return shouldLog;
   * }`</pre>
   *
   * See [RateLimitStatus] for more information on how to implement custom rate limiting in Flogger.
   *
   * @param status a rate limiting status, or `null` if the rate limiter was not active.
   * @return whether logging will occur based on the current combined state of active rate limiters.
   */
  protected fun updateRateLimiterStatus(status: KRateLimitStatus?): Boolean

  // ---- Log site injection (used by pre-processors and special cases) ----
  public final override fun withInjectedLogSite(logSite: KLogSite?): API

  @Suppress("deprecation")
  public final override fun withInjectedLogSite(
      internalClassName: String?,
      methodName: String?,
      encodedLineNumber: Int,
      sourceFileName: String?
  ): API

  // ---- Public logging API ----
  public final override fun isEnabled(): Boolean

  public final override fun <T> with(key: KMetadataKey<T>, value: T?): API

  public final override fun with(key: KMetadataKey<Boolean?>?): API

  public final override fun <T> per(key: T?, strategy: KLogPerBucketingStrategy<in T>): API

  public final override fun per(key: Enum<*>?): API

  public final override fun per(scopeProvider: KLoggingScopeProvider?): API

  public final override fun withCause(cause: Throwable?): API

  public final override fun withStackTrace(size: KStackSize?): API

  public final override fun every(n: Int): API

  public final override fun onAverageEvery(n: Int): API

  public final override fun atMostEvery(n: Int, unit: KTimeUnit?): API

  /*
   * Note that while all log statements look almost identical to each other, it is vital that we
   * keep the 'shouldLog()' call outside of the call to 'logImpl()' so we can decide whether or not
   * to abort logging before we do any varargs creation.
   */
  public final override fun log()

  public final override fun log(msg: String?)

  public final override fun log(msg: String?, p1: Any?)

  public final override fun log(msg: String?, p1: Any?, p2: Any?)

  public final override fun log(msg: String?, p1: Any?, p2: Any?, p3: Any?)

  public final override fun log(msg: String?, p1: Any?, p2: Any?, p3: Any?, p4: Any?)

  public final override fun log(msg: String?, p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any?)

  public final override fun log(
      msg: String?,
      p1: Any?,
      p2: Any?,
      p3: Any?,
      p4: Any?,
      p5: Any?,
      p6: Any?
  )

  public final override fun log(
      msg: String?,
      p1: Any?,
      p2: Any?,
      p3: Any?,
      p4: Any?,
      p5: Any?,
      p6: Any?,
      p7: Any?
  )

  public final override fun log(
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

  public final override fun log(
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

  public final override fun log(
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

  public final override fun log(
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

  public final override fun log(msg: String?, p1: Char)

  public final override fun log(msg: String?, p1: Byte)

  public final override fun log(msg: String?, p1: Short)

  public final override fun log(msg: String?, p1: Int)

  public final override fun log(msg: String?, p1: Long)

  public final override fun log(msg: String?, p1: Any?, p2: Boolean)

  public final override fun log(msg: String?, p1: Any?, p2: Char)

  public final override fun log(msg: String?, p1: Any?, p2: Byte)

  public final override fun log(msg: String?, p1: Any?, p2: Short)

  public final override fun log(msg: String?, p1: Any?, p2: Int)

  public final override fun log(msg: String?, p1: Any?, p2: Long)

  public final override fun log(msg: String?, p1: Any?, p2: Float)

  public final override fun log(msg: String?, p1: Any?, p2: Double)

  public final override fun log(msg: String?, p1: Boolean, p2: Any?)

  public final override fun log(msg: String?, p1: Char, p2: Any?)

  public final override fun log(msg: String?, p1: Byte, p2: Any?)

  public final override fun log(msg: String?, p1: Short, p2: Any?)

  public final override fun log(msg: String?, p1: Int, p2: Any?)

  public final override fun log(msg: String?, p1: Long, p2: Any?)

  public final override fun log(msg: String?, p1: Float, p2: Any?)

  public final override fun log(msg: String?, p1: Double, p2: Any?)

  public final override fun log(msg: String?, p1: Boolean, p2: Boolean)

  public final override fun log(msg: String?, p1: Char, p2: Boolean)

  public final override fun log(msg: String?, p1: Byte, p2: Boolean)

  public final override fun log(msg: String?, p1: Short, p2: Boolean)

  public final override fun log(msg: String?, p1: Int, p2: Boolean)

  public final override fun log(msg: String?, p1: Long, p2: Boolean)

  public final override fun log(msg: String?, p1: Float, p2: Boolean)

  public final override fun log(msg: String?, p1: Double, p2: Boolean)

  public final override fun log(msg: String?, p1: Boolean, p2: Char)

  public final override fun log(msg: String?, p1: Char, p2: Char)

  public final override fun log(msg: String?, p1: Byte, p2: Char)

  public final override fun log(msg: String?, p1: Short, p2: Char)

  public final override fun log(msg: String?, p1: Int, p2: Char)

  public final override fun log(msg: String?, p1: Long, p2: Char)

  public final override fun log(msg: String?, p1: Float, p2: Char)

  public final override fun log(msg: String?, p1: Double, p2: Char)

  public final override fun log(msg: String?, p1: Boolean, p2: Byte)

  public final override fun log(msg: String?, p1: Char, p2: Byte)

  public final override fun log(msg: String?, p1: Byte, p2: Byte)

  public final override fun log(msg: String?, p1: Short, p2: Byte)

  public final override fun log(msg: String?, p1: Int, p2: Byte)

  public final override fun log(msg: String?, p1: Long, p2: Byte)

  public final override fun log(msg: String?, p1: Float, p2: Byte)

  public final override fun log(msg: String?, p1: Double, p2: Byte)

  public final override fun log(msg: String?, p1: Boolean, p2: Short)

  public final override fun log(msg: String?, p1: Char, p2: Short)

  public final override fun log(msg: String?, p1: Byte, p2: Short)

  public final override fun log(msg: String?, p1: Short, p2: Short)

  public final override fun log(msg: String?, p1: Int, p2: Short)

  public final override fun log(msg: String?, p1: Long, p2: Short)

  public final override fun log(msg: String?, p1: Float, p2: Short)

  public final override fun log(msg: String?, p1: Double, p2: Short)

  public final override fun log(msg: String?, p1: Boolean, p2: Int)

  public final override fun log(msg: String?, p1: Char, p2: Int)

  public final override fun log(msg: String?, p1: Byte, p2: Int)

  public final override fun log(msg: String?, p1: Short, p2: Int)

  public final override fun log(msg: String?, p1: Int, p2: Int)

  public final override fun log(msg: String?, p1: Long, p2: Int)

  public final override fun log(msg: String?, p1: Float, p2: Int)

  public final override fun log(msg: String?, p1: Double, p2: Int)

  public final override fun log(msg: String?, p1: Boolean, p2: Long)

  public final override fun log(msg: String?, p1: Char, p2: Long)

  public final override fun log(msg: String?, p1: Byte, p2: Long)

  public final override fun log(msg: String?, p1: Short, p2: Long)

  public final override fun log(msg: String?, p1: Int, p2: Long)

  public final override fun log(msg: String?, p1: Long, p2: Long)

  public final override fun log(msg: String?, p1: Float, p2: Long)

  public final override fun log(msg: String?, p1: Double, p2: Long)

  public final override fun log(msg: String?, p1: Boolean, p2: Float)

  public final override fun log(msg: String?, p1: Char, p2: Float)

  public final override fun log(msg: String?, p1: Byte, p2: Float)

  public final override fun log(msg: String?, p1: Short, p2: Float)

  public final override fun log(msg: String?, p1: Int, p2: Float)

  public final override fun log(msg: String?, p1: Long, p2: Float)

  public final override fun log(msg: String?, p1: Float, p2: Float)

  public final override fun log(msg: String?, p1: Double, p2: Float)

  public final override fun log(msg: String?, p1: Boolean, p2: Double)

  public final override fun log(msg: String?, p1: Char, p2: Double)

  public final override fun log(msg: String?, p1: Byte, p2: Double)

  public final override fun log(msg: String?, p1: Short, p2: Double)

  public final override fun log(msg: String?, p1: Int, p2: Double)

  public final override fun log(msg: String?, p1: Long, p2: Double)

  public final override fun log(msg: String?, p1: Float, p2: Double)

  public final override fun log(msg: String?, p1: Double, p2: Double)

  public final override fun logVarargs(message: String?, varargs: Array<Any?>?)
}

// ---- LogData API ----

public expect val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.level: KLevel?

@Deprecated("")
public expect val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.timestampMicros: Long

public expect val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.timestampNanos: Long

public expect val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.loggerName: String?

public expect val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.logSite: KLogSite?

public expect val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.templateContext: KTemplateContext

public expect val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.arguments: Array<Any?>?

public expect val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.literalArguments: Any?

public expect val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.metadata: KMetadata?

/**
 * The predefined metadata keys used by the default logging API. Backend implementations can use
 * these to identify metadata added by the core logging API.
 */
// TODO: Reevaluate this whole strategy before open-sourcing.
public expect class KLogContextKey {
  public companion object {
    /**
     * The key associated with a [Throwable] cause to be associated with the log message. This value
     * is set by [LoggingApi.withCause].
     */
    public val LOG_CAUSE: KMetadataKey<Throwable>

    /**
     * The key associated with a rate limiting counter for "1-in-N" rate limiting. The value is set
     * by [LoggingApi.every].
     */
    public val LOG_EVERY_N: KMetadataKey<Int>

    /**
     * The key associated with a rate limiting counter for "1-in-N" randomly sampled rate limiting.
     * The value is set by [LoggingApi.onAverageEvery].
     */
    public val LOG_SAMPLE_EVERY_N: KMetadataKey<Int>

    /**
     * The key associated with a rate limiting period for "at most once every N" rate limiting. The
     * value is set by [LoggingApi.atMostEvery].
     */
    // TODO KFlogger: KRateLimitPeriod is default private-package visibility
    // public val LOG_AT_MOST_EVERY: KMetadataKey<KRateLimitPeriod>

    /**
     * The key associated with a count of rate limited logs. This is only public so backends can
     * reference the key to control formatting.
     */
    public val SKIPPED_LOG_COUNT: KMetadataKey<Int>

    /**
     * The key associated with a sequence of log site "grouping keys". These serve to specialize the
     * log site key to group the behaviour of stateful operations like rate limiting. This is used
     * by the `per()` methods and is only public so backends can reference the key to control
     * formatting.
     */
    public val LOG_SITE_GROUPING_KEY: KMetadataKey<Any>

    /**
     * The key associated with a `Boolean` value used to specify that the log statement must be
     * emitted.
     *
     * Forcing a log statement ensures that the `LoggerBackend` is passed the `LogData` for this log
     * statement regardless of the backend's log level or any other filtering or rate limiting which
     * might normally occur. If a log statement is forced, this key will be set immediately on
     * creation of the logging context and will be visible to both fluent methods and
     * post-processing.
     *
     * Filtering and rate-limiting methods must check for this value and should treat forced log
     * statements as not having had any filtering or rate limiting applied. For example, if the
     * following log statement was forced:
     * <pre>`logger.atInfo().withCause(e).atMostEvery(1, MINUTES).log("Message...");
     * `</pre> *
     *
     * it should behave as if the rate-limiting methods were never called, such as:
     * <pre>`logger.atInfo().withCause(e).log("Message...");
     * `</pre> *
     *
     * As well as no longer including any rate-limiting metadata for the forced log statement, this
     * also has the effect of never interfering with the rate-limiting of this log statement for
     * other callers.
     *
     * The decision of whether to force a log statement is expected to be made based upon debug
     * values provded by the logger which come from a scope greater than the log statement itself.
     * Thus it makes no sense to provide a public method to set this value programmatically for a
     * log statement.
     */
    public val WAS_FORCED: KMetadataKey<Boolean>

    /**
     * The key associated with any injected [Tags].
     *
     * If tags are injected, they are added after post-processing if the log site is enabled. Thus
     * they are not available to the `postProcess()` method itself. The rationale is that a log
     * statement's behavior should only be affected by code at the log site (other than "forcing"
     * log statements, which is slightly a special case).
     *
     * Tags can be added at the log site, although this should rarely be necessary and using normal
     * log message arguments is always the preferred way to indicate unstrctured log data. Users
     * should never build new [Tags] instances just to pass them into a log statement.
     */
    // TODO KFlogger: val TAGS: KMetadataKey<com.buenaflor.kflogger.context.Tags>

    /**
     * Key associated with the metadata for specifying additional stack information with a log
     * statement.
     */
    public val CONTEXT_STACK_SIZE: KMetadataKey<KStackSize>
  }
}
