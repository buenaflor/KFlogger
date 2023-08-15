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
 * A structured representation of formatting options compatible with printf style formatting.
 *
 * This class is immutable and thread safe.
 */
public expect class KFormatOptions {
  /**
   * Returns a possibly new FormatOptions instance possibly containing a subset of the formatting
   * information. This is useful if a backend implementation wishes to create formatting options
   * that ignore some of the specified formatting information.
   *
   * @param allowedFlags A mask of flag values to be retained in the returned instance. Use
   *   [.ALL_FLAGS] to retain all flag values, or `0` to suppress all flags.
   * @param allowWidth specifies whether to include width in the returned instance.
   * @param allowPrecision specifies whether to include precision in the returned instance.
   */
  public fun filter(allowedFlags: Int, allowWidth: Boolean, allowPrecision: Boolean): KFormatOptions

  /** Returns true if this instance has only default formatting options. */
  public fun isDefault(): Boolean

  /**
   * Validates these options according to the allowed criteria and checks for inconsistencies in
   * flag values.
   *
   * Note that there is not requirement for options used internally in custom message parsers to be
   * validated, but any format options passed through the `ParameterVisitor` interface must be valid
   * with respect to the associated [FormatChar] instance.
   *
   * @param allowedFlags a bit mask specifying a subset of the printf flags that are allowed for
   *   these options.
   * @param allowPrecision true if these options are allowed to have a precision value specified.
   * @return true if these options are valid given the specified publicraints.
   */
  public fun validate(allowedFlags: Int, allowPrecision: Boolean): Boolean

  /**
   * Validates these options as if they were being applied to the given [FormatChar] and checks for
   * inconsistencies in flag values.
   *
   * Note that there is not requirement for options used internally in custom message parsers to be
   * validated, but any format options passed through the
   * [ParameterVisitor][com.buenaflor.kflogger.parameter.ParameterVisitor] interface must be valid
   * with respect to the associated [FormatChar] instance.
   *
   * @param formatChar the formatting rule to check these options against.
   * @return true if these options are valid for the given format.
   */
  public fun areValidFor(formatChar: KFormatChar): Boolean

  /**
   * Corresponds to printf flag '-' (incompatible with '0').
   *
   * Logging backends may ignore this flag, though it does provide some visual clarity when aligning
   * values.
   */
  public fun shouldLeftAlign(): Boolean

  /**
   * Corresponds to printf flag '#'.
   *
   * Logging backends should honor this flag for hex or octal, as it is a common way to avoid
   * ambiguity when formatting non-decimal values.
   */
  public fun shouldShowAltForm(): Boolean

  /**
   * Corresponds to printf flag '0'.
   *
   * Logging backends should honor this flag, as it is very commonly used to format hexadecimal or
   * octal values to allow specific bit values to be calculated.
   */
  public fun shouldShowLeadingZeros(): Boolean

  /**
   * Corresponds to printf flag '+'.
   *
   * Logging backends are free to ignore this flag, though it does provide some visual clarity when
   * tabulating certain types of values.
   */
  public fun shouldPrefixPlusForPositiveValues(): Boolean

  /**
   * Corresponds to printf flag ' '.
   *
   * Logging backends are free to ignore this flag, though if they choose to support
   * [.shouldPrefixPlusForPositiveValues] then it is advisable to support this as well.
   */
  public fun shouldPrefixSpaceForPositiveValues(): Boolean

  /**
   * Corresponds to printf flag ','.
   *
   * Logging backends are free to select the locale in which the formatting will occur or ignore
   * this flag altogether.
   */
  public fun shouldShowGrouping(): Boolean

  /**
   * Corresponds to formatting with an upper-case format character.
   *
   * Logging backends are free to ignore this flag.
   */
  public fun shouldUpperCase(): Boolean

  /**
   * Appends the data for this options instance in a printf compatible form to the given buffer.
   * This method neither appends the leading `%` symbol nor a format type character. Output is
   * written in the form `[width][.precision][flags]` and for the default instance, nothing is
   * appended.
   *
   * @param out The output buffer to which the options are appended.
   */
  public fun appendPrintfOptions(out: StringBuilder): StringBuilder

  override fun equals(other: Any?): Boolean

  override fun hashCode(): Int

  public companion object {
    /**
     * A formatting flag which specifies that for signed numeric output, positive values should be
     * prefixed with an ASCII space (`' '`). This corresponds to the `' '` printf flag and is valid
     * for all signed numeric types.
     */
    public val FLAG_PREFIX_SPACE_FOR_POSITIVE_VALUES: Int

    /**
     * A formatting flag which specifies that output should be shown in a type dependent alternate
     * form. This corresponds to the `'#'` printf flag and is valid for:
     * * Octal (%o) and hexadecimal (%x, %X) formatting, where it specifies that the radix should be
     *   shown.
     * * Floating point (%f) and exponential (%e, %E, %a, %A) formatting, where it specifies that a
     *   decimal separator should always be shown.
     */
    public val FLAG_SHOW_ALT_FORM: Int

    /**
     * A formatting flag which specifies that for signed numeric output, negative values should be
     * surrounded by parentheses. This corresponds to the `'('` printf flag and is valid for all
     * signed numeric types.
     */
    public val FLAG_USE_PARENS_FOR_NEGATIVE_VALUES: Int

    /**
     * A formatting flag which specifies that for signed numeric output, positive values should be
     * prefixed with an ASCII plus (`'+'`). This corresponds to the `'+'` printf flag and is valid
     * for all signed numeric types.
     */
    public val FLAG_PREFIX_PLUS_FOR_POSITIVE_VALUES: Int

    /**
     * A formatting flag which specifies that for non-exponential, base-10, numeric output a
     * grouping separator (often a ',') should be used. This corresponds to the `','` printf flag
     * and is valid for:
     * * Decimal (%d) and unsigned (%u) formatting.
     * * Float (%f) and general scientific notation (%g, %G)
     */
    public val FLAG_SHOW_GROUPING: Int

    /**
     * A formatting flag which specifies that output should be left-aligned within the minimum
     * available width. This corresponds to the `'-'` printf flag and is valid for all `FormatChar`
     * instances, though it must be specified in conjunction with a width value.
     */
    public val FLAG_LEFT_ALIGN: Int

    /**
     * A formatting flag which specifies that numeric output should be padding with leading zeros as
     * necessary to fill the minimum width. This corresponds to the `'0'` printf flag and is valid
     * for all numeric types, though it must be specified in conjunction with a width value.
     */
    public val FLAG_SHOW_LEADING_ZEROS: Int

    /**
     * A formatting flag which specifies that output should be upper-cased after all other
     * formatting. This corresponds to having an upper-case format character and is valud for any
     * type with an upper case variant.
     */
    public val FLAG_UPPER_CASE: Int

    /** A mask of all allowed formatting flags. Useful when filtering options via [.filter]. */
    public val ALL_FLAGS: Int

    /** The value used to specify that either width or precision were not specified. */
    public val UNSET: Int

    /** Returns the default options singleton instance. */
    public fun getDefault(): KFormatOptions

    /** Creates a options instance with the given values. */
    public fun of(flags: Int, width: Int, precision: Int): KFormatOptions

    /**
     * Parses a sub-sequence of a log message to extract and return its options. Note that callers
     * cannot rely on this method producing new instances each time it is called as caching of
     * common option values may occur.
     *
     * @param message the original log message in which the formatting options have been identified.
     * @param pos the index of the first character to parse.
     * @param end the index after the last character to be parsed.
     * @return the parsed options instance.
     * @throws ParseException if the specified sub-sequence of the string could not be parsed.
     */
    // TODO KFlogger: @Throws(com.buenaflor.kflogger.parser.ParseException::class)
    public fun parse(message: String, pos: Int, end: Int, isUpperCase: Boolean): KFormatOptions

    /** Internal helper method for creating a bit-mask from a string of valid flag characters. */
    public fun parseValidFlags(flagChars: String, hasUpperVariant: Boolean): Int

    // Helper to check for legal combinations of flags.
    public fun checkFlagConsistency(flags: Int, hasWidth: Boolean): Boolean
  }
}

/**
 * Returns the precision for these options, or [.UNSET] if not specified. This is a non-negative
 * decimal integer, usually used to restrict the number of characters, but its precise meaning is
 * dependent on the formatting rule it is applied to.
 */
public expect fun KFormatOptions.precision(): Int
