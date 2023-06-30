package com.buenaflor.kflogger

/**
 * Key for logging semi-structured metadata values.
 *
 *
 * Metadata keys can be used to provide log statements with strongly typed values which can be
 * read and interpreted by logging backends or other logs related tools. This mechanism is intended
 * for values with specific semantics and should not be seen as a replacement for logging arguments
 * as part of a formatted log message.
 *
 *
 * Examples of where using `MetadataKey` is suitable are:
 *
 *
 *  * Logging a value with special semantics (e.g. values that are handled specially by the
 * logger backend).
 *  * Passing configuration to a specific logger backend to modify behaviour for individual log
 * statements or all log statements in a `ScopedLoggingContext`.
 *  * Logging a structured value in many places with consistent formatting (e.g. so it can later
 * be re-parsed by logs related tools).
 *
 *
 *
 * If you just want to log an general "key value pair" in a small number of log statements, it is
 * still better to just do something like `log("key=%s", value)`.
 *
 *
 * Metadata keys are expected to be singleton constants, and should never be allocated at the log
 * site itself. Even though they are expected to be singletons, comparing keys should be done via
 * `equals()` (rather than '==') since this will be safe in cases where non-singleton keys
 * exist, and is just as fast if the keys are singletons.
 *
 *
 * It is strongly recommended that any public [MetadataKey] instances are defined as `public static final` fields in a top-level or nested class which does no logging. Ideally a
 * separate class would be defined to hold only the keys, since this allows keys to be loaded very
 * early in the logging [Platform] lifecycle without risking any static initialization issues.
 *
 *
 * Custom subclasses of `MetadataKey` which override either of the protected [.emit]
 * methods should take care to avoid calling any code which might trigger logging since this could
 * lead to unexpected recusrion, especially if the key is being logged as part of a `ScopedLoggingContext`. While there is protection against unbounded reentrant logging in Flogger,
 * it is still best practice to avoid it where possible.
 *
 *
 * Metadata keys are passed to a log statement via the `with()` method, so it can aid
 * readability to choose a name for the constant field which reads "fluently" as part of the log
 * statement. For example:
 *
 * <pre>`// Prefer this...
 * logger.atInfo().with(FILE_LOGGING_FOR, user).log("User specific log message...");
 * // to...
 * logger.atInfo().with(SET_LOGGING_TO_USER_FILE, user).log("User specific log message...");
`</pre> *
 *
 *
 * Logger backends can act upon metadata present in log statements to modify behaviour. Any
 * metadata entries that are not handled by a backend explicitly are, by default, rendered as part
 * of the log statement in a default format.
 *
 *
 * Note that some metadata entries are handled prior to being processed by the backend (e.g. rate
 * limiting), but a metadata entry remains present to record the fact that rate limiting was
 * enabled.
 */
expect open class KMetadataKey<T> {
    /**
     * Callback interface to handle additional contextual `Metadata` in log statements. This
     * interface is only intended to be implemented by logger backend classes as part of handling
     * metadata, and should not be used in any general application code, other than to implement the
     * [KMetadataKey.emit] method in this class.
     */
    interface KeyValueHandler {
        /** Handle a single key/value pair of contextual metadata for a log statement.  */
        fun handle(key: String?, value: Any?)
    }

    /* TODO
    /**
     * Constructor for custom key subclasses. Most use-cases will not require the use of custom keys,
     * but occasionally it can be useful to create a specific subtype to control the formatting of
     * values or to have a family of related keys with a common parent type.
     */
    protected constructor(label: String, clazz: java.lang.Class<out T>, canRepeat: Boolean) : this(
        label,
        clazz,
        canRepeat,
        true
    )*/

    /** Cast an arbitrary value to the type of this key.  */
    fun cast(value: Any?): T

    /** Whether this key can be used to set more than one value in the metadata.  */
    fun canRepeat(): Boolean

    /**
     * Emits one or more key/value pairs for the given metadata value. Call this method in preference
     * to using [.emitRepeated] directly to protect against unbounded reentrant logging.
     */
    fun safeEmit(value: T, kvh: KeyValueHandler)

    /**
     * Emits one or more key/value pairs for a sequence of repeated metadata values. Call this method
     * in preference to using [.emitRepeated] directly to protect against unbounded reentrant
     * logging.
     */
    fun safeEmitRepeated(values: Iterator<T>, kvh: KeyValueHandler)

    /**
     * Override this method to provide custom logic for emitting one or more key/value pairs for a
     * given metadata value (call [.safeEmit] from logging code to
     * actually emit values).
     *
     *
     * By default this method simply emits the given value with this key's label, but it can be
     * customized key/value pairs if necessary.
     *
     *
     * Note that if multiple key/value pairs are emitted, the following best-practice should be
     * followed:
     *
     *
     *  * Key names should be of the form `"<label>.<suffix>"`.
     *  * Suffixes should only contain lower case ASCII letters and underscore (i.e. [a-z_]).
     *
     *
     *
     * This method is called as part of logs processing and could be invoked a very large number of
     * times in performance critical code. Implementations must be very careful to avoid calling any
     * code which might risk deadlocks, stack overflow, concurrency issues or performance problems. In
     * particular, implementations of this method should be careful to avoid:
     *
     *
     *  * Calling any code which could log using the same `MetadataKey` instance (unless you
     * implement protection against reentrant calling in this method).
     *  * Calling code which might block (e.g. performing file I/O or acquiring locks).
     *  * Allocating non-trivial amounds of memory (e.g. recording values in an unbounded data
     * structure).
     *
     *
     *
     * If you do implement a `MetadataKey` with non-trivial value processing, you should
     * always make it very clear in the documentation that the key may not be suitable for widespread
     * use.
     *
     *
     * By default this method just calls `out.handle(getLabel(), value)`.
     */
    protected open fun emit(value: T, kvh: KeyValueHandler)

    /**
     * Override this method to provide custom logic for emitting one or more key/value pairs for a
     * sequence of metadata values (call [.safeEmitRepeated] from
     * logging code to actually emit values).
     *
     *
     * Emits one or more key/value pairs for a sequence of repeated metadata values. By default
     * this method simply calls [.emit] once for each value, in order.
     * However it could be overridden to treat the sequence of values for a repeated key as a single
     * entity (e.g. by joining elements with a separator).
     *
     *
     * See the [.emit] method for additional caveats for custom
     * implementations.
     */
    protected open fun emitRepeated(values: Iterator<T>, kvh: KeyValueHandler)

    // Prevent subclasses changing the singleton semantics of keys.
    final override fun hashCode(): Int

    final override fun equals(other: Any?): Boolean

    // Prevent subclasses using toString() for anything unexpected.
    final override fun toString(): String

    /* TODO
companion object {
/**
 * Creates a key for a single piece of metadata. If metadata is set more than once using this key
 * for the same log statement, the last set value will be the one used, and other values will be
 * ignored (although callers should never rely on this behavior).
 *
 *
 * Key instances behave like singletons, and two key instances with the same label will still
 * be considered distinct. The recommended approach is to always assign `MetadataKey`
 * instances to static final constants.
 */
fun <T> single(label: String, clazz: java.lang.Class<out T>): MetadataKey<T> {
    return MetadataKey(label, clazz, false, false)
}

/**
 * Creates a key for a repeated piece of metadata. If metadata is added more than once using this
 * key for a log statement, all values will be retained as key/value pairs in the order they were
 * added.
 *
 *
 * Key instances behave like singletons, and two key instances with the same label will still
 * be considered distinct. The recommended approach is to always assign `MetadataKey`
 * instances to static final constants.
 */
fun <T> repeated(label: String, clazz: java.lang.Class<T>): MetadataKey<T> {
    return MetadataKey(label, clazz, true, false)
}
    }
     */
}

/**
 * Returns a short, human readable text label which will prefix the metadata in cases where it is
 * formatted as part of the log message.
 */
expect val <T> KMetadataKey<T>.label: String

/**
 * Returns a 64-bit bloom filter mask for this metadata key, usable by backend implementations to
 * efficiently determine uniqueness of keys (e.g. for deduplication and grouping). This value is
 * calculated on the assumption that there are normally not more than 10 distinct metadata keys
 * being processed at any time. If more distinct keys need to be processed using this Bloom Filter
 * mask, it will result in a higher than optimal false-positive rate.
 */
expect val <T> KMetadataKey<T>.bloomFilterMask: Long
