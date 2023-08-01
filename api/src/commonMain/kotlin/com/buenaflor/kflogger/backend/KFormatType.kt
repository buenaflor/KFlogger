package com.buenaflor.kflogger.backend

/** The general formatting type of any one of the predefined `FormatChar` instances. */
public expect enum class KFormatType {
  /** General formatting that can be applied to any type. */
  GENERAL,

  /** Formatting that can be applied to any boolean type. */
  BOOLEAN,

  /**
   * Formatting that can be applied to Character or any integral type that can be losslessly
   * converted to an int and for which [Character.isValidCodePoint] returns true.
   */
  CHARACTER,

  /**
   * Formatting that can be applied to any integral Number type. Logging backends must support Byte,
   * Short, Integer, Long and BigInteger but may also support additional numeric types directly. A
   * logging backend that encounters an unknown numeric type should fall back to using `toString()`.
   */
  INTEGRAL,

  /**
   * Formatting that can be applied to any Number type. Logging backends must support all the
   * integral types as well as Float, Double and BigDecimal, but may also support additional numeric
   * types directly. A logging backend that encounters an unknown numeric type should fall back to
   * using `toString()`.
   */
  FLOAT;

  /**
   * True if the notion of a specified precision value makes sense to this format type. Precision is
   * specified in addition to width and can control the resolution of a formatting operation (e.g.
   * how many digits to output after the decimal point for floating point values).
   */
  internal open fun supportsPrecision(): Boolean

  public abstract fun canFormat(arg: Any?): Boolean
}
