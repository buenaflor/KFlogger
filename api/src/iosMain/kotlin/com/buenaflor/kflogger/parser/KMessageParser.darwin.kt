/*
 * Copyright (C) 2012 The Flogger Authors.
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
package com.buenaflor.kflogger.parser

/**
 * Base class from which any specific message parsers are derived (e.g. [PrintfMessageParser] and
 * [BraceStyleMessageParser]).
 */
public actual abstract class KMessageParser {
  /**
   * Abstract parse method implemented by specific subclasses to modify parsing behavior.
   *
   * Note that when extending parsing behavior, it is expected that specific parsers such as
   * [DefaultPrintfMessageParser] or [DefaultBraceStyleMessageParser] will be sub-classed. Extending
   * this class directly is only necessary when an entirely new type of format needs to be supported
   * (which should be extremely rare).
   *
   * Implementations of this method are required to invoke the [MessageBuilder.addParameterImpl]
   * method of the supplied builder once for each parameter place-holder in the message.
   */
  @Throws(KParseException::class)
  protected actual abstract fun <T> parseImpl(builder: KMessageBuilder<T>?)

  /**
   * Appends the unescaped literal representation of the given message string (assumed to be escaped
   * according to this parser's escaping rules). This method is designed to be invoked from a
   * callback method in a [MessageBuilder] instance.
   *
   * @param out the destination into which to append characters
   * @param message the escaped log message
   * @param start the start index (inclusive) in the log message
   * @param end the end index (exclusive) in the log message
   */
  public actual abstract fun unescape(out: StringBuilder?, message: String?, start: Int, end: Int)

  public actual companion object {
    /**
     * The maximum allowed index (this should correspond to the MAX_ALLOWED_WIDTH in
     * [FormatOptions][com.buenaflor.kflogger.backend.FormatOptions] because at times it is
     * ambiguous as to which is being parsed).
     */
    public actual val MAX_ARG_COUNT: Int
      get() = TODO("Not yet implemented")
  }
}
