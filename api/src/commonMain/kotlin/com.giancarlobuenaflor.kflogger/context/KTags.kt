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
package com.giancarlobuenaflor.kflogger.context

/**
 * Immutable tags which can be attached to log statements via platform specific injection
 * mechanisms.
 *
 * A tag is either a "simple" tag, added via [Builder.addTag] or a tag with a value, added via one
 * of the `addTag(name,value)` methods. When thinking of tags as a `Map<String, Set<Object>>`, the
 * value of a "simple" tag is the empty set.
 *
 * Tag values can be of several simple types and are held in a stable, sorted order within a `Tags`
 * instance. In other words it never matters in which order two `Tags` instances are merged.
 *
 * When tags are merged, the result is the union of the values. This is easier to explain When
 * thinking of tags as a `Map<String, Set<Object>>`, where "merging" means taking the union of the
 * `Set` associated with the tag name. In particular, for a given tag name:
 * * Adding the same value for a given tag twice has no additional effect.
 * * Adding a simple tag twice has no additional effect.
 * * Adding a tag with a value is also implicitly like adding a simple tag with the same name.
 *
 * The [.toString] implementation of this class provides a human readable, machine parsable
 * representation of the tags.
 */
public expect class KTags {
  /** Returns an immutable map containing the tag values. */
  public fun asMap(): Map<String, Set<Any>>

  /** Returns whether this instance is empty. */
  public fun isEmpty(): Boolean

  /** Merges two tags instances, combining values for any name contained in both. */
  public fun merge(other: KTags): KTags

  override fun equals(other: Any?): Boolean

  override fun hashCode(): Int

  /**
   * Returns human readable representation of the tags. This is not a stable representation and may
   * change over time. If you need to format tags reliably for logging, you should not rely on this
   * method.
   */
  override fun toString(): String

  public companion object {
    /** Returns a new builder for adding tags. */
    public fun builder(): KTagsBuilder

    /** Returns the immutable empty tags instance. */
    public fun empty(): KTags

    /**
     * Returns a single tag without needing to use the builder API. Where multiple tags are needed,
     * it is always better to use the builder directly.
     */
    public fun of(name: String, value: String): KTags

    /**
     * Returns a single tag without needing to use the builder API. Where multiple tags are needed,
     * it is always better to use the builder directly.
     */
    public fun of(name: String, value: Boolean): KTags

    /**
     * Returns a single tag without needing to use the builder API. Where multiple tags are needed,
     * it is always better to use the builder directly.
     */
    public fun of(name: String, value: Long): KTags

    /**
     * Returns a single tag without needing to use the builder API. Where multiple tags are needed,
     * it is always better to use the builder directly.
     */
    public fun of(name: String, value: Double): KTags
  }
}

/** A mutable builder for tags. */
public expect class KTagsBuilder() {

  /**
   * Adds an empty tag, ensuring that the given name exists in the tag map with at least an empty
   * set of values. Adding the same name more than once has no effect.
   *
   * When viewed as a `Set`, the value for an empty tag is just the empty set. However if other
   * values are added for the same name, the set of values will no longer be empty and the call to
   * [.addTag] will have had no lasting effect.
   */
  public fun addTag(name: String): KTagsBuilder

  /**
   * Adds a string value for the given name, ensuring that the values for the given name contain at
   * least this value. Adding the same name/value pair more than once has no effect.
   */
  public fun addTag(name: String, value: String?): KTagsBuilder

  /**
   * Adds a boolean value for the given name, ensuring that the values for the given name contain at
   * least this value. Adding the same name/value pair more than once has no effect.
   */
  public fun addTag(name: String, value: Boolean): KTagsBuilder

  /**
   * Adds a long value for the given name, ensuring that the values for the given name contain at
   * least this value. Adding the same name/value pair more than once has no effect.
   *
   * Note however that for numeric values, differing types (long/double) are always considered
   * distinct, so invoking both `addTag("foo", 1234L)` and `addTag("foo", 1234.0D)` will result in
   * two values for the tag.
   */
  public fun addTag(name: String, value: Long): KTagsBuilder

  /**
   * Adds a double value for the given name, ensuring that the values for the given name contain at
   * least this value. Adding the same name/value pair more than once has no effect.
   *
   * Note however that for numeric values, differing types (long/double) are always considered
   * distinct, so invoking both `addTag("foo", 1234L)` and `addTag("foo", 1234.0D)` will result in
   * two values for the tag.
   */
  public fun addTag(name: String, value: Double): KTagsBuilder

  /** Returns an immutable tags instance. */
  public fun build(): KTags

  override fun toString(): String
}
