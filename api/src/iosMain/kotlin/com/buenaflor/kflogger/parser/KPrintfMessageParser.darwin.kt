package com.buenaflor.kflogger.parser

/**
 * A specialized [MessageParser] for processing log messages in printf style, as used by
 * [String.format]. This is an abstract parser which knows how to process and extract placeholder
 * terms at a high level, but does not impose its own semantics for place-holder types.
 *
 * Typically you should not subclass this class, but instead subclass [DefaultPrintfMessageParser],
 * which provides compatibility with [String.format].
 */
public actual abstract class KPrintfMessageParser : KMessageParser() {
  /**
   * Parses a single printf-like term from a log message into a message template builder.
   *
   * A simple example of an implicit parameter (the argument index is not specified):
   * <pre>
   * message: "Hello %s World"
   * termStart: 6 ───┚╿╿
   * specStart: 7 ────┤│
   * formatStart: 7 ──╯│
   * return: 8 ────────╯
   * </pre> *
   * If this case there is no format specification, so `specStart == formatStart`.
   *
   * A complex example with an explicit index:
   * <pre>
   * message: "Hello %2$10d World"
   * termStart: 6 ───┚  ╿ ╿╿
   * specStart: 9 ──────╯ ││
   * formatStart: 11 ─────╯│
   * return: 12 ───────────╯
   * </pre> *
   * Note that in this example the given index will be 1 (rather than 2) because printf specifies
   * indices using a 1-based scheme, but internally they are 0-based.
   *
   * @param builder the message template builder.
   * @param index the zero-based argument index for the parameter.
   * @param message the complete log message string.
   * @param termStart the index of the initial '%' character that starts the term.
   * @param specStart the index of the first format specification character (after any optional
   *   index specification).
   * @param formatStart the index of the (first) format character in the term.
   * @return the index after the last character of the term.
   */
  @Throws(KParseException::class)
  protected actual abstract fun parsePrintfTerm(
      builder: KMessageBuilder<*>?,
      index: Int,
      message: String?,
      termStart: Int,
      specStart: Int,
      formatStart: Int
  ): Int

  actual final override fun unescape(out: StringBuilder?, message: String?, start: Int, end: Int) {
    TODO()
  }

  @Throws(KParseException::class)
  actual final override fun <T> parseImpl(builder: KMessageBuilder<T>?) {
    TODO()
  }

  public actual companion object {
    /**
     * Returns the system newline separator avoiding any issues with security exceptions or
     * "suspicious" values. The only allowed return values are "\n" (default), "\r" or "\r\n".
     */
    internal actual fun getSafeSystemNewline(): String {
      TODO()
    }

    /**
     * Returns the index of the first unescaped '%' character in message starting at pos (or -1 if
     * not found).
     */
    // VisibleForTesting
    @Throws(KParseException::class)
    internal actual fun nextPrintfTerm(message: String, pos: Int): Int {
      TODO()
    }

    /**
     * Unescapes the characters in the sub-string `s.substring(start, end)` according to printf
     * style formatting rules.
     */
    // VisibleForTesting
    internal actual fun unescapePrintf(out: StringBuilder, message: String, start: Int, end: Int) {
      TODO()
    }
  }
}
