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
public actual class KTags {
  /** Returns an immutable map containing the tag values. */
  public actual fun asMap(): Map<String, Set<Any>> {
    TODO("Not yet implemented")
  }

  /** Returns whether this instance is empty. */
  public actual fun isEmpty(): Boolean {
    TODO("Not yet implemented")
  }

  /** Merges two tags instances, combining values for any name contained in both. */
  public actual fun merge(other: KTags): KTags {
    TODO("Not yet implemented")
  }

  public actual companion object {
    /** Returns a new builder for adding tags. */
    public actual fun builder(): KTagsBuilder {
      TODO("Not yet implemented")
    }

    /** Returns the immutable empty tags instance. */
    public actual fun empty(): KTags {
      TODO("Not yet implemented")
    }

    /**
     * Returns a single tag without needing to use the builder API. Where multiple tags are needed,
     * it is always better to use the builder directly.
     */
    public actual fun of(name: String, value: String): KTags {
      TODO("Not yet implemented")
    }

    /**
     * Returns a single tag without needing to use the builder API. Where multiple tags are needed,
     * it is always better to use the builder directly.
     */
    public actual fun of(name: String, value: Boolean): KTags {
      TODO("Not yet implemented")
    }

    /**
     * Returns a single tag without needing to use the builder API. Where multiple tags are needed,
     * it is always better to use the builder directly.
     */
    public actual fun of(name: String, value: Long): KTags {
      TODO("Not yet implemented")
    }

    /**
     * Returns a single tag without needing to use the builder API. Where multiple tags are needed,
     * it is always better to use the builder directly.
     */
    public actual fun of(name: String, value: Double): KTags {
      TODO("Not yet implemented")
    }
  }
}

/** A mutable builder for tags. */
public actual class KTagsBuilder {
  /**
   * Adds an empty tag, ensuring that the given name exists in the tag map with at least an empty
   * set of values. Adding the same name more than once has no effect.
   *
   * When viewed as a `Set`, the value for an empty tag is just the empty set. However if other
   * values are added for the same name, the set of values will no longer be empty and the call to
   * [.addTag] will have had no lasting effect.
   */
  public actual fun addTag(name: String): KTagsBuilder {
    TODO("Not yet implemented")
  }

  /**
   * Adds a string value for the given name, ensuring that the values for the given name contain at
   * least this value. Adding the same name/value pair more than once has no effect.
   */
  public actual fun addTag(name: String, value: String?): KTagsBuilder {
    TODO("Not yet implemented")
  }

  /**
   * Adds a boolean value for the given name, ensuring that the values for the given name contain at
   * least this value. Adding the same name/value pair more than once has no effect.
   */
  public actual fun addTag(name: String, value: Boolean): KTagsBuilder {
    TODO("Not yet implemented")
  }

  /**
   * Adds a long value for the given name, ensuring that the values for the given name contain at
   * least this value. Adding the same name/value pair more than once has no effect.
   *
   * Note however that for numeric values, differing types (long/double) are always considered
   * distinct, so invoking both `addTag("foo", 1234L)` and `addTag("foo", 1234.0D)` will result in
   * two values for the tag.
   */
  public actual fun addTag(name: String, value: Long): KTagsBuilder {
    TODO("Not yet implemented")
  }

  /**
   * Adds a double value for the given name, ensuring that the values for the given name contain at
   * least this value. Adding the same name/value pair more than once has no effect.
   *
   * Note however that for numeric values, differing types (long/double) are always considered
   * distinct, so invoking both `addTag("foo", 1234L)` and `addTag("foo", 1234.0D)` will result in
   * two values for the tag.
   */
  public actual fun addTag(name: String, value: Double): KTagsBuilder {
    TODO("Not yet implemented")
  }

  /** Returns an immutable tags instance. */
  public actual fun build(): KTags {
    TODO("Not yet implemented")
  }
}
