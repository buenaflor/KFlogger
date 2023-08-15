package com.buenaflor.kflogger

import com.buenaflor.kflogger.backend.KLogData
import com.buenaflor.kflogger.backend.KMetadata
import com.buenaflor.kflogger.backend.KTemplateContext
import com.buenaflor.kflogger.context.KTags
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
public actual abstract class KLogContext<LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> :
    KLoggingApi<API>, KLogData {
  /** The template context if formatting is required (set only after post-processing). */
  private var templateContext: KTemplateContext? = null

  // TODO: Aggressively attempt to reduce the number of fields in this instance.
  /** The log level of the log statement that this context was created for. */
  private var level: KLevel

  /** The timestamp of the log statement that this context is associated with. */
  private var timestampNanos: Long

  /**
   * A simple token used to identify cases where a single literal value is logged. Note that this
   * instance must be unique and it is important not to replace this with `""` or any other value
   * than might be interned and be accessible to code outside this class.
   */
  private val LITERAL_VALUE_MESSAGE = String()

  /** The log arguments (set only after post-processing). */
  private var args: Array<out Any>? = null

  /**
   * Creates a logging context with the specified level, and with a timestamp obtained from the
   * configured logging [Platform].
   *
   * @param level the log level for this log statement.
   * @param isForced whether to force this log statement (see [.wasForced] for details).
   */
  // TODO KFlogger: Timestamp
  protected actual constructor(level: KLevel, isForced: Boolean) : this(level, isForced, 0L)

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
  protected actual constructor(level: KLevel, isForced: Boolean, timestampNanos: Long) {
    this.level = level
    this.timestampNanos = timestampNanos
  }

  /**
   * Returns the current API (which is just the concrete sub-type of this instance). This is
   * returned by fluent methods to continue the fluent call chain.
   */
  protected actual abstract fun api(): API

  // ---- Logging Context Constants ----
  /**
   * Returns the logger which created this context. This is implemented as an abstract method to
   * save a field in every context.
   */
  protected actual abstract fun getLogger(): LOGGER

  /**
   * Returns the constant no-op logging API, which can be returned by fluent methods in extended
   * logging contexts to efficiently disable logging. This is implemented as an abstract method to
   * save a field in every context.
   */
  protected actual abstract fun noOp(): API

  /** Returns the message parser used for all log statements made through this logger. */
  protected actual abstract fun getMessageParser(): KMessageParser

  // ---- LogData API ----

  public actual final override fun wasForced(): Boolean {
    // TODO KFlogger
    return false
  }

  // ---- Mutable Metadata ----
  /**
   * Adds the given key/value pair to this logging context. If the key cannot be repeated, and there
   * is already a value for the key in the metadata, then the existing value is replaced, otherwise
   * the value is added at the end of the metadata.
   *
   * @param key the metadata key (see [LogData]).
   * @param value the metadata value.
   */
  protected actual fun <T> addMetadata(key: KMetadataKey<T>?, value: T) {
    TODO("Not yet implemented")
  }

  /**
   * Removes all key/value pairs with the specified key. Note that this method does not resize any
   * underlying backing arrays or other storage as logging contexts are expected to be short lived.
   *
   * @param key the metadata key (see [LogData]).
   */
  protected actual fun removeMetadata(key: KMetadataKey<*>?) {
    TODO("Not yet implemented")
  }

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
  protected actual open fun postProcess(logSiteKey: KLogSiteKey?): Boolean {
    TODO()
  }

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
  protected actual fun updateRateLimiterStatus(status: KRateLimitStatus?): Boolean {
    TODO()
  }

  // ---- Log site injection (used by pre-processors and special cases) ----
  public actual final override fun withInjectedLogSite(logSite: KLogSite?): API {
    TODO()
  }

  @Suppress("deprecation")
  public actual final override fun withInjectedLogSite(
      internalClassName: String?,
      methodName: String?,
      encodedLineNumber: Int,
      sourceFileName: String?
  ): API {
    TODO()
  }

  // ---- Public logging API ----
  public actual final override fun isEnabled(): Boolean {
    return getLogger().isLoggable(level)
  }

  public actual final override fun <T> with(key: KMetadataKey<T>, value: T?): API {
    TODO()
  }

  public actual final override fun with(key: KMetadataKey<Boolean?>?): API {
    TODO()
  }

  public actual final override fun <T> per(key: T?, strategy: KLogPerBucketingStrategy<in T>): API {
    TODO()
  }

  public actual final override fun per(key: Enum<*>?): API {
    TODO()
  }

  public actual final override fun per(scopeProvider: KLoggingScopeProvider?): API {
    TODO()
  }

  public actual final override fun withCause(cause: Throwable?): API {
    TODO()
  }

  public actual final override fun withStackTrace(size: KStackSize?): API {
    TODO()
  }

  public actual final override fun every(n: Int): API {
    TODO()
  }

  public actual final override fun onAverageEvery(n: Int): API {
    TODO()
  }

  public actual final override fun atMostEvery(n: Int, unit: KTimeUnit?): API {
    TODO()
  }

  private fun logImpl(message: String?, vararg args: Any?) {
    this.args = args as Array<out Any>?
    // TODO KFlogger: Handle lazyargs
    if (message != LITERAL_VALUE_MESSAGE) {
      if (message == null) {
        throw NullPointerException("message must not be null")
      }
      templateContext = KTemplateContext(getMessageParser(), message)
    }
    // TODO KFlogger: Add tags and metadata
    getLogger().write(this)
  }

  /*
   * Note that while all log statements look almost identical to each other, it is vital that we
   * keep the 'shouldLog()' call outside of the call to 'logImpl()' so we can decide whether or not
   * to abort logging before we do any varargs creation.
   */
  public actual final override fun log() {
    logImpl(LITERAL_VALUE_MESSAGE, "")
  }

  public actual final override fun log(msg: String?) {
    logImpl(LITERAL_VALUE_MESSAGE, msg)
  }

  public actual final override fun log(msg: String?, p1: Any?) {
    logImpl(msg, p1)
  }

  public actual final override fun log(msg: String?, p1: Any?, p2: Any?) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Any?, p2: Any?, p3: Any?) {
    logImpl(msg, p1, p2, p3)
  }

  public actual final override fun log(msg: String?, p1: Any?, p2: Any?, p3: Any?, p4: Any?) {
    logImpl(msg, p1, p2, p3, p4)
  }

  public actual final override fun log(
      msg: String?,
      p1: Any?,
      p2: Any?,
      p3: Any?,
      p4: Any?,
      p5: Any?
  ) {
    logImpl(msg, p1, p2, p3, p4, p5)
  }

  public actual final override fun log(
      msg: String?,
      p1: Any?,
      p2: Any?,
      p3: Any?,
      p4: Any?,
      p5: Any?,
      p6: Any?
  ) {
    logImpl(msg, p1, p2, p3, p4, p5, p6)
  }

  public actual final override fun log(
      msg: String?,
      p1: Any?,
      p2: Any?,
      p3: Any?,
      p4: Any?,
      p5: Any?,
      p6: Any?,
      p7: Any?
  ) {
    logImpl(msg, p1, p2, p3, p4, p5, p6, p7)
  }

  public actual final override fun log(
      msg: String?,
      p1: Any?,
      p2: Any?,
      p3: Any?,
      p4: Any?,
      p5: Any?,
      p6: Any?,
      p7: Any?,
      p8: Any?
  ) {
    logImpl(msg, p1, p2, p3, p4, p5, p6, p7, p8)
  }

  public actual final override fun log(
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
  ) {
    logImpl(msg, p1, p2, p3, p4, p5, p6, p7, p8, p9)
  }

  public actual final override fun log(
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
  ) {
    logImpl(msg, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10)
  }

  public actual final override fun log(
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
  ) {
    // Manually create a new varargs array and copy the parameters in.
    val params = arrayOfNulls<Any>(rest.size + 10)
    params[0] = p1
    params[1] = p2
    params[2] = p3
    params[3] = p4
    params[4] = p5
    params[5] = p6
    params[6] = p7
    params[7] = p8
    params[8] = p9
    params[9] = p10
    logImpl(msg, rest.copyInto(params, 10))
  }

  public actual final override fun log(msg: String?, p1: Char) {
    logImpl(msg, p1)
  }

  public actual final override fun log(msg: String?, p1: Byte) {
    logImpl(msg, p1)
  }

  public actual final override fun log(msg: String?, p1: Short) {
    logImpl(msg, p1)
  }

  public actual final override fun log(msg: String?, p1: Int) {
    logImpl(msg, p1)
  }

  public actual final override fun log(msg: String?, p1: Long) {
    logImpl(msg, p1)
  }

  public actual final override fun log(msg: String?, p1: Any?, p2: Boolean) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Any?, p2: Char) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Any?, p2: Byte) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Any?, p2: Short) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Any?, p2: Int) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Any?, p2: Long) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Any?, p2: Float) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Any?, p2: Double) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Boolean, p2: Any?) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Char, p2: Any?) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Byte, p2: Any?) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Short, p2: Any?) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Int, p2: Any?) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Long, p2: Any?) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Float, p2: Any?) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Double, p2: Any?) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Boolean, p2: Boolean) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Char, p2: Boolean) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Byte, p2: Boolean) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Short, p2: Boolean) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Int, p2: Boolean) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Long, p2: Boolean) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Float, p2: Boolean) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Double, p2: Boolean) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Boolean, p2: Char) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Char, p2: Char) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Byte, p2: Char) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Short, p2: Char) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Int, p2: Char) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Long, p2: Char) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Float, p2: Char) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Double, p2: Char) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Boolean, p2: Byte) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Char, p2: Byte) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Byte, p2: Byte) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Short, p2: Byte) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Int, p2: Byte) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Long, p2: Byte) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Float, p2: Byte) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Double, p2: Byte) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Boolean, p2: Short) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Char, p2: Short) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Byte, p2: Short) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Short, p2: Short) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Int, p2: Short) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Long, p2: Short) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Float, p2: Short) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Double, p2: Short) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Boolean, p2: Int) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Char, p2: Int) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Byte, p2: Int) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Short, p2: Int) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Int, p2: Int) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Long, p2: Int) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Float, p2: Int) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Double, p2: Int) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Boolean, p2: Long) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Char, p2: Long) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Byte, p2: Long) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Short, p2: Long) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Int, p2: Long) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Long, p2: Long) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Float, p2: Long) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Double, p2: Long) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Boolean, p2: Float) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Char, p2: Float) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Byte, p2: Float) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Short, p2: Float) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Int, p2: Float) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Long, p2: Float) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Float, p2: Float) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Double, p2: Float) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Boolean, p2: Double) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Char, p2: Double) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Byte, p2: Double) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Short, p2: Double) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Int, p2: Double) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Long, p2: Double) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Float, p2: Double) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun log(msg: String?, p1: Double, p2: Double) {
    logImpl(msg, p1, p2)
  }

  public actual final override fun logVarargs(message: String?, varargs: Array<Any?>?) {
    varargs?.copyOf()?.let { logImpl(message!!, it) }
  }

  actual final override fun getLevel(): KLevel {
    return level
  }

  @Deprecated("")
  actual final override fun getTimestampMicros(): Long {
    TODO("Not yet implemented")
  }

  actual final override fun getTimestampNanos(): Long {
    TODO("Not yet implemented")
  }

  actual final override fun getLoggerName(): String? {
    TODO("Not yet implemented")
  }

  actual final override fun getLogSite(): KLogSite {
    TODO("Not yet implemented")
  }

  actual final override fun getTemplateContext(): KTemplateContext? {
    return templateContext
  }

  actual final override fun getArguments(): Array<Any?>? {
    if (templateContext == null) {
      throw IllegalStateException("cannot get arguments unless a template context exists")
    }
    return args as Array<Any?>
  }

  actual final override fun getLiteralArgument(): Any? {
    if (templateContext != null) {
      throw IllegalStateException("cannot get literal argument if a template context exists")
    }
    return args?.get(0)
  }

  /**
   * Returns any additional metadata for this log statement.
   *
   * When called outside of the logging backend, this method may return different values at
   * different times (ie, it may initially return a shared static "empty" metadata object and later
   * return a different implementation). As such it is not safe to cache the instance returned by
   * this method or to attempt to cast it to any particular implementation.
   */
  actual final override fun getMetadata(): KMetadata {
    TODO("Not yet implemented")
  }
}

/**
 * The predefined metadata keys used by the default logging API. Backend implementations can use
 * these to identify metadata added by the core logging API.
 */
// TODO: Reevaluate this whole strategy before open-sourcing.
public actual class KLogContextKey {
  public actual companion object {

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
    public actual val TAGS: KMetadataKey<KTags>
      get() = TODO()

    /**
     * The key associated with a [Throwable] cause to be associated with the log message. This value
     * is set by [LoggingApi.withCause].
     */
    public actual val LOG_CAUSE: KMetadataKey<Throwable>
      get() = TODO("Not yet implemented")

    /**
     * The key associated with a rate limiting counter for "1-in-N" rate limiting. The value is set
     * by [KLoggingApi.every].
     */
    public actual val LOG_EVERY_N: KMetadataKey<Int>
      get() = TODO("Not yet implemented")

    /**
     * The key associated with a rate limiting counter for "1-in-N" randomly sampled rate limiting.
     * The value is set by [LoggingApi.onAverageEvery].
     */
    public actual val LOG_SAMPLE_EVERY_N: KMetadataKey<Int>
      get() = TODO("Not yet implemented")

    /**
     * The key associated with a rate limiting period for "at most once every N" rate limiting. The
     * value is set by [LoggingApi.atMostEvery].
     */
    // TODO KFlogger: KRateLimitPeriod is default private-package visibility
    // public val LOG_AT_MOST_EVERY: KMetadataKey<KRateLimitPeriod>
    //    get() = TODO("Not yet implemented")

    /**
     * The key associated with a count of rate limited logs. This is only public so backends can
     * reference the key to control formatting.
     */
    public actual val SKIPPED_LOG_COUNT: KMetadataKey<Int>
      get() = TODO("Not yet implemented")

    /**
     * The key associated with a sequence of log site "grouping keys". These serve to specialize the
     * log site key to group the behaviour of stateful operations like rate limiting. This is used
     * by the `per()` methods and is only public so backends can reference the key to control
     * formatting.
     */
    public actual val LOG_SITE_GROUPING_KEY: KMetadataKey<Any>
      get() = TODO("Not yet implemented")

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
    public actual val WAS_FORCED: KMetadataKey<Boolean>
      get() = TODO("Not yet implemented")

    /**
     * Key associated with the metadata for specifying additional stack information with a log
     * statement.
     */
    public actual val CONTEXT_STACK_SIZE: KMetadataKey<KStackSize>
      get() = TODO("Not yet implemented")
  }
}
