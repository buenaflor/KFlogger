package com.giancarlobuenaflor.kflogger.parser

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
  protected actual final override fun <T> parseImpl(builder: KMessageBuilder<T>) {
    TODO()
  }

  public actual companion object {
    private val SYSTEM_NEWLINE = getSafeSystemNewline()

    /**
     * Returns the system newline separator avoiding any issues with security exceptions or
     * "suspicious" values. The only allowed return values are "\n" (default), "\r" or "\r\n".
     */
    internal actual fun getSafeSystemNewline(): String {
      // TODO
      return "\n"
    }

    /**
     * Returns the index of the first unescaped '%' character in message starting at pos (or -1 if
     * not found).
     */
    // VisibleForTesting
    @Throws(KParseException::class)
    internal actual fun nextPrintfTerm(message: String, pos: Int): Int {
      var pos = pos
      while (pos < message.length) {
        if (message[pos++] != '%') {
          continue
        }
        if (pos < message.length) {
          val c = message[pos]
          if (c == '%' || c == 'n') {
            // We encountered '%%' or '%n', so keep going (these will be unescaped later).
            pos += 1
            continue
          }
          // We were pointing at the character after the '%', so adjust back by one.
          return pos - 1
        }
        // We ran off the end while looking for the character after the first '%'.
        // TODO: should be KParseException
        throw IllegalArgumentException("trailing unquoted '%' character")
      }
      // We never found another unescaped '%'.
      return -1
    }

    /**
     * Unescapes the characters in the sub-string `s.substring(start, end)` according to printf
     * style formatting rules.
     */
    // VisibleForTesting
    internal actual fun unescapePrintf(out: StringBuilder, message: String, start: Int, end: Int) {
      var start = start
      var pos = start
      while (pos < end) {
        if (message[pos++] != '%') {
          continue
        }
        if (pos == end) {
          // Ignore unexpected trailing '%'.
          break
        }
        val chr = message[pos]
        if (chr == '%') {
          // Append the section up to and including the first '%'.
          out.append(message, start, pos)
        } else if (chr == 'n') {
          // %n encountered, rewind one position to not emit leading '%' and emit newline.
          out.append(message, start, pos - 1)
          out.append(KPrintfMessageParser.SYSTEM_NEWLINE)
        } else {
          // A single unescaped '%' is ignored and left in the output as-is.
          continue
        }
        // Increment the position and reset the start point after the last processed character.
        start = ++pos
      }
      // Append the last section (if it's non empty).
      if (start < end) {
        out.append(message, start, end)
      }
    }
  }
}
