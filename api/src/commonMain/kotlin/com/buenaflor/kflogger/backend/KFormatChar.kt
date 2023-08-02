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
package com.buenaflor.kflogger.backend

/**
 * An enum representing the printf-like formatting characters that must be supported by all logging
 * backends. It is important to note that while backends must accept any of these format specifiers,
 * they are not obliged to implement all specified formatting behavior.
 *
 * The default term formatter takes care of supporting all these options when expressed in their
 * normal '%X' form (including flags, width and precision). Custom messages parsers must convert
 * arguments into one of these forms before passing then through to the backend.
 */
public expect enum class KFormatChar {
  /**
   * Formats the argument in a manner specific to the chosen logging backend. In many cases this
   * will be equivalent to using `STRING`, but it allows backend implementations to log more
   * structured representations of known types.
   *
   * This is a non-numeric format with an upper-case variant.
   */
  STRING,

  /**
   * Formats the argument as a boolean.
   *
   * This is a non-numeric format with an upper-case variant.
   */
  BOOLEAN,

  /**
   * Formats a Unicode code-point. This formatting rule can be applied to any character or integral
   * numeric value, providing that [Character.isValidCodePoint] returns true. Note that if the
   * argument cannot be represented losslessly as an integer, it must be considered invalid.
   *
   * This is a non-numeric format with an upper-case variant.
   */
  CHAR,

  /**
   * Formats the argument as a decimal integer.
   *
   * This is a numeric format.
   */
  DECIMAL,

  /**
   * Formats the argument as an unsigned octal integer.
   *
   * This is a numeric format.
   *
   * '(' is only supported for [java.math.BigInteger] or [java.math.BigDecimal]
   */
  OCTAL,

  /**
   * Formats the argument as an unsigned hexadecimal integer.
   *
   * This is a numeric format with an upper-case variant.
   *
   * '(' is only supported for [java.math.BigInteger] or [java.math.BigDecimal]
   */
  HEX,

  /**
   * Formats the argument as a signed decimal floating value.
   *
   * This is a numeric format.
   */
  FLOAT,

  /**
   * Formats the argument using computerized scientific notation.
   *
   * This is a numeric format with an upper-case variant.
   */
  EXPONENT,

  /**
   * Formats the argument using general scientific notation.
   *
   * This is a numeric format with an upper-case variant.
   */
  GENERAL,

  /**
   * Formats the argument using hexadecimal exponential form. This formatting option is primarily
   * useful when debugging issues with the precise bit-wise representation of doubles because no
   * rounding of the value takes place.
   *
   * This is a numeric format with an upper-case variant.
   */
  // Note: This could be optimized with Double.toHexString() but this parameter is hardly ever used.
  EXPONENT_HEX;

  public companion object {
    /**
     * Returns the FormatChar instance associated with the given printf format specifier. If the
     * given character is not an ASCII letter, a runtime exception is thrown.
     */
    public fun of(c: Char): KFormatChar?
  }
}

/**
 * Returns the allowed flag characters as a string. This is package private to hide the precise
 * implementation of how we parse and manage formatting options.
 */
internal expect val KFormatChar.allowedFlags: Int

/** Returns the general format type for this character. */
public expect val KFormatChar.type: KFormatType

public expect val KFormatChar.defaultFormatString: String
