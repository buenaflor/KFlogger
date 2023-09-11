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
package com.giancarlobuenaflor.kflogger.backend

/**
 * An enum representing the printf-like formatting characters that must be supported by all logging
 * backends. It is important to note that while backends must accept any of these format specifiers,
 * they are not obliged to implement all specified formatting behavior.
 *
 * The default term formatter takes care of supporting all these options when expressed in their
 * normal '%X' form (including flags, width and precision). Custom messages parsers must convert
 * arguments into one of these forms before passing then through to the backend.
 */
public actual enum class KFormatChar {
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

  public actual companion object {
    // A direct mapping from character offset to FormatChar instance. Have all 26 letters accounted
    // for because we know that the caller has already checked that this is an ASCII letter.
    // This mapping needs to be fast as it's called for every argument in every log message.
    private val MAP: Array<com.giancarlobuenaflor.kflogger.backend.KFormatChar?> =
        arrayOfNulls<com.giancarlobuenaflor.kflogger.backend.KFormatChar>(26)

    // Returns whether a given ASCII letter is lower case.
    private fun isLowerCase(letter: Char): Boolean {
      return letter.code and 0x20 != 0
    }

    // Returns the numeric index [0-25] of a given ASCII letter (upper or lower case). If the given
    // value is not an ASCII letter, the returned value is not in the range 0-25.
    private fun indexOf(letter: Char): Int {
      return (letter.code or 0x20) - 'a'.code
    }

    /**
     * Returns the FormatChar instance associated with the given printf format specifier. If the
     * given character is not an ASCII letter, a runtime exception is thrown.
     */
    public actual fun of(c: Char): com.giancarlobuenaflor.kflogger.backend.KFormatChar? {
      // Get from the map by converting the char to lower-case (which is the most common case by
      // far).
      // If the given value wasn't an ASCII letter then the index will be out-of-range, but when
      // called by the parser, it's always guaranteed to be an ASCII letter (but perhaps not a valid
      // format character).

      // Get from the map by converting the char to lower-case (which is the most common case by
      // far).
      // If the given value wasn't an ASCII letter then the index will be out-of-range, but when
      // called by the parser, it's always guaranteed to be an ASCII letter (but perhaps not a valid
      // format character).
      val fc: com.giancarlobuenaflor.kflogger.backend.KFormatChar? = MAP[indexOf(c)]
      if (isLowerCase(c)) {
        // If we were given a lower case char to find, we're done (even if the result is null).
        return fc
      }
      // Otherwise handle the case where we found a lower-case format char but no upper-case one.
      // Otherwise handle the case where we found a lower-case format char but no upper-case one.
      return if (fc != null && fc.hasUpperCaseVariant()) fc else null
    }
  }

  private fun hasUpperCaseVariant(): Boolean {
    return (allowedFlags and KFormatOptions.FLAG_UPPER_CASE) != 0
  }
}

/**
 * Returns the allowed flag characters as a string. This is package private to hide the precise
 * implementation of how we parse and manage formatting options.
 */
internal actual val com.giancarlobuenaflor.kflogger.backend.KFormatChar.allowedFlags: Int
  get() {
    TODO()
  }

/** Returns the general format type for this character. */
public actual val com.giancarlobuenaflor.kflogger.backend.KFormatChar.type: KFormatType
  get() {
    TODO()
  }

public actual val com.giancarlobuenaflor.kflogger.backend.KFormatChar.defaultFormatString: String
  get() {
    TODO()
  }
