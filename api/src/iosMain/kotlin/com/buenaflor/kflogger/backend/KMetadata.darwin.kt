package com.buenaflor.kflogger.backend

/**
 * A sequence of metadata key/value pairs which can be associated to a log statement, either
 * directly via methods in the fluent API, of as part of a scoped logging context.
 *
 * Metadata keys can "single valued" or "repeating" based on [KMetadataKey.canRepeat], but it is
 * permitted for a `Metadata` implementation to retain multiple single valued keys, and in that
 * situation the key at the largest index is the one which should be used.
 *
 * Multiple `Metadata` instances can be merged, in order, to provide a final sequence for a log
 * statement. When `Metadata` instance are merged, the result is just the concatenation of the
 * sequence of key/value pairs, and this is what results in the potential for mutliple single valued
 * keys to exist.
 *
 * If the value of a single valued key is required, the [.findValue] method should be used to look
 * it up. For all other metadata processing, a [MetadataProcessor] should be created to ensure that
 * scope and log site metadata can be merged correctly.
 */
actual abstract class KMetadata {
  /** Returns the number of key/value pairs for this instance. */
  actual abstract fun size(): Int

  /**
   * Returns the key for the Nth piece of metadata.
   *
   * @throws IndexOutOfBoundsException if either `n < 0` or {n >= getCount()}.
   */
  actual abstract fun getKey(n: Int): KMetadataKey<*>?

  /**
   * Returns the non-null value for the Nth piece of metadata.
   *
   * @throws IndexOutOfBoundsException if either `n < 0` or {n >= getCount()}.
   */
  actual abstract fun getValue(n: Int): Any?

  /**
   * Returns the first value for the given single valued metadata key, or null if it does not exist.
   *
   * @throws NullPointerException if `key` is `null`.
   */
  actual abstract fun <T> findValue(key: KMetadataKey<T>?): T?

  actual companion object {
    /** Returns an immutable [KMetadata] that has no items. */
    actual fun empty(): KMetadata {
      TODO("Not yet implemented")
    }
  }
}