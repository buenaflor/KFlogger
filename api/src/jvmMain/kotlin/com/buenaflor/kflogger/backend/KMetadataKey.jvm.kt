package com.buenaflor.kflogger.backend

import com.buenaflor.kflogger.MetadataKey

@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
actual typealias KMetadataKey<T> = MetadataKey<T>

/**
 * Returns a short, human readable text label which will prefix the metadata in cases where it is
 * formatted as part of the log message.
 */
actual val <T> KMetadataKey<T>.label: String
  get() = label

/**
 * Returns a 64-bit bloom filter mask for this metadata key, usable by backend implementations to
 * efficiently determine uniqueness of keys (e.g. for deduplication and grouping). This value is
 * calculated on the assumption that there are normally not more than 10 distinct metadata keys
 * being processed at any time. If more distinct keys need to be processed using this Bloom Filter
 * mask, it will result in a higher than optimal false-positive rate.
 */
actual val <T> KMetadataKey<T>.bloomFilterMask: Long
  get() = bloomFilterMask
