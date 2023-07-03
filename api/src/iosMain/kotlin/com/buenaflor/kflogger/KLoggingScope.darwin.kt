package com.buenaflor.kflogger

actual abstract class KLoggingScope actual constructor(label: String) {
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
  protected actual abstract fun specialize(key: KLogSiteKey?): KLogSiteKey?

  actual companion object {
    /**
     * Creates a scope which automatically removes any associated keys from [LogSiteMap]s when it's
     * garbage collected. The given label is used only for debugging purposes and may appear in log
     * statements, it should not contain any user data or other runtime information.
     */
    actual fun create(label: String?): KLoggingScope {
      TODO("Not yet implemented")
    }
  }
}
