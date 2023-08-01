/*
 * Copyright (C) 2017 The Flogger Authors.
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
package com.buenaflor.kflogger.parameter

import com.buenaflor.kflogger.backend.KFormatChar
import com.buenaflor.kflogger.backend.KFormatOptions

/** A visitor of log message arguments, dispatched by `Parameter` instances. */
// TODO: When all other refactoring done, rename to ArgumentVisitor
public expect interface KParameterVisitor {
  /**
   * Visits a log message argument with formatting specified by `%s`, `%d` etc...
   *
   * Note that this method may still visit arguments which represent date/time values if the format
   * is not explicit (e.g. `log("time=%s", dateTime)`).
   *
   * @param value the non-null log message argument.
   * @param format the printf format specifier.
   * @param options formatting options.
   */
  public fun visit(value: Any?, format: KFormatChar?, options: KFormatOptions?)

  /**
   * Visits a date/time log message argument with formatting specified by `%t` or similar.
   *
   * Note that because this method is called based on the specified format (and not the argument
   * type) it may visit arguments whose type is not a known date/time value. This is necessary to
   * permit new date/time types to be supported by different logging backends (e.g. JodaTime).
   *
   * @param value the non-null log message argument.
   * @param format the date/time format specifier.
   * @param options formatting options.
   */
  public fun visitDateTime(value: Any?, format: KDateTimeFormat?, options: KFormatOptions?)

  /**
   * Visits a log message argument for which formatting has already occurred. This method is only
   * invoked when non-printf message formatting is used (e.g. brace style formatting).
   *
   * This method is intended for use by `Parameter` implementations which describe formatting rules
   * which cannot by represented by either [FormatChar] or [DateTimeFormat]. This method discards
   * formatting and type information, and the visitor implementation may choose to reexamine the
   * type of the original argument if doing structural logging.
   *
   * @param value the original non-null log message argument.
   * @param formatted the formatted representation of the argument
   */
  public fun visitPreformatted(value: Any?, formatted: String?)

  /**
   * Visits a missing argument. This method is called when there is no corresponding value for the
   * parameter's argument index.
   */
  public fun visitMissing()

  /** Visits a null argument. */
  public fun visitNull()
}
