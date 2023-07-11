/*
 * Copyright (C) 2016 The Flogger Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.buenaflor.kflogger.backend

import com.buenaflor.kflogger.MetadataKey
import org.checkerframework.checker.nullness.compatqual.NullableDecl

/**
 * A sequence of metadata key/value pairs which can be associated to a log statement, either
 * directly via methods in the fluent API, of as part of a scoped logging context.
 *
 * Metadata keys can "single valued" or "repeating" based on [MetadataKey.canRepeat], but it is
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
public actual abstract class Metadata {
  // This is a static nested class as opposed to an anonymous class assigned to a constant field in
  // order to decouple it's classload when Metadata is loaded. Android users are particularly
  // careful about unnecessary class loading, and we've used similar mechanisms in Guava (see
  // CharMatchers)
  private class Empty : Metadata() {
    override fun size(): Int {
      return 0
    }

    override fun getKey(n: Int): MetadataKey<*> {
      throw IndexOutOfBoundsException("cannot read from empty metadata")
    }

    override fun getValue(n: Int): Any {
      throw IndexOutOfBoundsException("cannot read from empty metadata")
    }

    @NullableDecl
    override fun <T> findValue(key: MetadataKey<T>?): T? {
      return null
    }

    companion object {
      val INSTANCE = Empty()
    }
  }

  /** Returns the number of key/value pairs for this instance. */
  public actual abstract fun size(): Int

  /**
   * Returns the key for the Nth piece of metadata.
   *
   * @throws IndexOutOfBoundsException if either `n < 0` or {n >= getCount()}.
   */
  public actual abstract fun getKey(n: Int): MetadataKey<*>?

  /**
   * Returns the non-null value for the Nth piece of metadata.
   *
   * @throws IndexOutOfBoundsException if either `n < 0` or {n >= getCount()}.
   */
  public actual abstract fun getValue(n: Int): Any?

  /**
   * Returns the first value for the given single valued metadata key, or null if it does not exist.
   *
   * @throws NullPointerException if `key` is `null`.
   */
  // TODO(dbeaumont): Make this throw an exception for repeated keys.
  @NullableDecl public actual abstract fun <T> findValue(key: MetadataKey<T>?): T?

  public actual companion object {
    /** Returns an immutable [Metadata] that has no items. */
    @JvmStatic
    public actual fun empty(): Metadata {
      return Empty.INSTANCE
    }
  }
}
