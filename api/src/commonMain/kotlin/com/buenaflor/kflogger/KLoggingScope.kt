package com.buenaflor.kflogger

/**
 * An opaque scope marker which can be attached to log sites to provide "per scope" behaviour for
 * stateful logging operations (e.g. rate limiting).
 *
 * Scopes are provided via the [Provider] interface and found by looking for the current
 * [ScopedLoggingContexts][com.buenaflor.kflogger.context.ScopedLoggingContext].
 *
 * Stateful fluent logging APIs which need to look up per log site information (e.g. rate limit
 * state) should do so via a [LogSiteMap] using the [LogSiteKey] passed into the
 * [ ][LogContext.postProcess] method. If scopes are present in the log site [ ] then the log site
 * key provided to the `postProcess()` method will already be specialized to take account of any
 * scopes present.
 *
 * Note that scopes have no effect when applied to stateless log statements (e.g. log statements
 * without rate limiting) since the log site key for that log statement will not be used in any
 * maps.
 */
public expect abstract class KLoggingScope
/**
 * Creates a basic scope with the specified label. Custom subclasses of `KLoggingScope` must manage
 * their own lifecycles to avoid leaking memory and polluting [LogSiteMap]s with unused keys.
 */
protected constructor(label: String) {

  /**
   * Returns a specialization of the given key which accounts for this scope instance. Two
   * specialized keys should compare as [Object.equals] if and only if they are specializations from
   * the same log site, with the same sequence of scopes applied.
   *
   * The returned instance:
   * * Must be an immutable "value type".
   * * Must not compare as [Object.equals] to the given key.
   * * Should have a different [Object.hashCode] to the given key.
   * * Should be efficient and lightweight.
   *
   * As such it is recommended that the [SpecializedLogSiteKey.of] method is used in
   * implementations, passing in a suitable qualifier (which need not be the scope itself, but must
   * be unique per scope).
   */
  protected abstract fun specialize(key: KLogSiteKey?): KLogSiteKey?

  /**
   * Registers "hooks" which should be called when this scope is "closed". The hooks are intended to
   * remove the keys associated with this scope from any data structures they may be held in, to
   * avoid leaking allocations.
   *
   * Note that a key may be specialized with several scopes and the first scope to be closed will
   * remove it from any associated data structures (conceptually the scope that a log site is called
   * from is the intersection of all the currently active scopes which apply to it).
   */
  // TODO KFlogger: protected abstract fun onClose(removalHook: java.lang.Runnable?)

  final override fun toString(): String

  public companion object {
    /**
     * Creates a scope which automatically removes any associated keys from [LogSiteMap]s when it's
     * garbage collected. The given label is used only for debugging purposes and may appear in log
     * statements, it should not contain any user data or other runtime information.
     */
    // TODO: Strongly consider making the label a compile time constant.
    public fun create(label: String): KLoggingScope
  }
}
