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

package com.buenaflor.kflogger

/**
 * Enum values to be passed into [GoogleLoggingApi.withStackTrace] to control the maximum number of
 * stack trace elements created.
 *
 * Note that the precise value returned by [.getMaxDepth] may change over time, but it can be
 * assumed that `SMALL <= MEDIUM <= LARGE <= FULL`.
 */
public actual enum class StackSize(
    /**
     * Returns the maximum stack depth to create when adding contextual stack information to a log
     * statement.
     *
     * Note that the precise number of stack elements emitted for the enum values might change over
     * time, but it can be assumed that `NONE < SMALL <= MEDIUM <= LARGE <= FULL`.
     */
    public val maxDepth: Int
) {
  /**
   * Produces a small stack suitable for more fine grained debugging. For performance reasons, this
   * is the only stack size suitable for log statements at level `INFO` or finer, but is may also be
   * useful for `WARNING` level log statements in cases where context is not as important. For
   * `SEVERE` log statements, it is advised to use a stack size of [.MEDIUM] or above.
   *
   * Requesting a small stack trace for log statements which occur under normal circumstances is
   * acceptable, but may affect performance. Consider using [GoogleLoggingApi.withStackTrace] in
   * conjunction with rate limiting methods, such as [LoggingApi.atMostEvery], to mitigate
   * performance issues.
   *
   * The current maximum size of a `SMALL` stack trace is 10 elements, but this may change.
   */
  SMALL(10),

  /**
   * Produces a medium sized stack suitable for providing contextual information for most log
   * statements at `WARNING` or above. There should be enough stack trace elements in a `MEDIUM`
   * stack to provide sufficient debugging context in most cases.
   *
   * Requesting a medium stack trace for any log statements which can occur regularly under normal
   * circumstances is not recommended.
   *
   * The current maximum size of a `MEDIUM` stack trace is 20 elements, but this may change.
   */
  MEDIUM(20),

  /**
   * Produces a large stack suitable for providing highly detailed contextual information. This is
   * most useful for `SEVERE` log statements which might be processed by external tools and subject
   * to automated analysis.
   *
   * Requesting a large stack trace for any log statement which can occur under normal circumstances
   * is not recommended.
   *
   * The current maximum size of a `LARGE` stack trace is 50 elements, but this may change.
   */
  LARGE(50),

  /**
   * Provides the complete stack trace. This is included for situations in which it is known that
   * the upper-most elements of the stack are definitely required for analysis.
   *
   * Requesting a full stack trace for any log statement which can occur under normal circumstances
   * is not recommended.
   */
  FULL(-1),

  /**
   * Provides no stack trace, making the `withStackTrace()` method an effective no-op. This is
   * useful when your stack size is conditional. For example:
   * <pre> `logger.atWarning()
   * .withStackTrace(showTrace ? StackSize.MEDIUM : StackSize.NONE)
   * .log("message");
   * `</pre> *
   */
  NONE(0)
}
