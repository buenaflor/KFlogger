/*
 * Copyright (C) 2014 The Flogger Authors.
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
package com.buenaflor.kflogger

import com.buenaflor.kflogger.backend.KMetadata

/**
 * Immutable metadata for rate limiting based on a fixed count. This corresponds to the
 * LOG_AT_MOST_EVERY metadata key in [LogData]. Unlike the metadata for `every(N)`, we need to use a
 * wrapper class here to preserve the time unit information.
 */
// This is not an inner class of DurationRateLimiter because inner class do not work with typealias.
internal expect class KRateLimitPeriod internal constructor(n: Int, unit: KTimeUnit) {
  fun toNanos(): Long

  override fun toString(): String

  override fun hashCode(): Int

  override fun equals(obj: Any?): Boolean
}

/**
 * Rate limiter to support `atMostEvery(N, units)` functionality.
 *
 * Instances of this class are created for each unique [LogSiteKey] for which rate limiting via the
 * `LOG_AT_MOST_EVERY` metadata key is required. This class implements `RateLimitStatus` as a
 * mechanism for resetting the rate limiter state.
 *
 * Instances of this class are thread safe.
 */
internal expect class KDurationRateLimiter : KRateLimitStatus {

  /**
   * Checks whether the current time stamp is after the rate limiting period and if so, updates the
   * time stamp and returns true. This is invoked during post-processing if a rate limiting duration
   * was set via [LoggingApi.atMostEvery].
   */
  // Visible for testing.
  fun checkLastTimestamp(timestampNanos: Long, period: KRateLimitPeriod): KRateLimitStatus

  // Reset function called to move the limiter out of the "pending" state. We do this by negating
  // the timestamp (which was already negated when we entered the pending state, so we restore it
  // to a positive value which moves us back into the "limiting" state).
  public override fun reset()

  companion object {
    /**
     * Creates a period for rate limiting for the specified duration. This is invoked by the
     * [ ][LogContext.atMostEvery] method to create a metadata value.
     */
    fun newRateLimitPeriod(n: Int, unit: KTimeUnit): KRateLimitPeriod

    /**
     * Returns whether the log site should log based on the value of the `LOG_AT_MOST_EVERY`
     * metadata value and the current log site timestamp.
     */
    fun check(
        metadata: KMetadata,
        logSiteKey: KLogSiteKey?,
        timestampNanos: Long
    ): KRateLimitStatus?
  }
}
