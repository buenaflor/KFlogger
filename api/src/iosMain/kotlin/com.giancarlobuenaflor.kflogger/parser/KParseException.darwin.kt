package com.giancarlobuenaflor.kflogger.parser

public actual class KParseException(errorMessage: String, logMessage: String) :
    RuntimeException(errorMessage) {
  public companion object {
    // The prefix/suffix to show when an error snippet is truncated (eg, "...ello [%Q] Worl...").
    // If the snippet starts or ends the message then no ellipsis is shown (eg, "...ndex=[%Q]").
    private const val ELLIPSIS = "..."

    // The length of the snippet to show before and after the error. Fewer characters will be shown
    // if the error is near the start/end of the log message and more characters will be shown if
    // adding the ellipsis would have made things longer. The maximum prefix/suffix of the snippet
    // is (SNIPPET_LENGTH + ELLIPSIS.length()).
    private const val SNIPPET_LENGTH = 5

    /** Helper to format a human readable error message for this exception. */
    private fun msg(
        errorMessage: String,
        logMessage: String,
        errorStart: Int,
        errorEnd: Int
    ): String {
      var errorEnd = errorEnd
      if (errorEnd < 0) {
        errorEnd = logMessage.length
      }
      val out = StringBuilder(errorMessage).append(": ")
      if (errorStart > SNIPPET_LENGTH + ELLIPSIS.length) {
        out.append(ELLIPSIS).append(logMessage, errorStart - SNIPPET_LENGTH, errorStart)
      } else {
        out.append(logMessage, 0, errorStart)
      }
      out.append('[').append(logMessage.substring(errorStart, errorEnd)).append(']')
      if (logMessage.length - errorEnd > SNIPPET_LENGTH + ELLIPSIS.length) {
        out.append(logMessage, errorEnd, errorEnd + SNIPPET_LENGTH).append(ELLIPSIS)
      } else {
        out.append(logMessage, errorEnd, logMessage.length)
      }
      return out.toString()
    }

    /**
     * Creates a new parse exception for situations in which the position of the error is known.
     *
     * @param errorMessage the user error message.
     * @param logMessage the original log message.
     * @param position the index of the invalid character in the log message.
     * @return the parser exception.
     */
    public fun atPosition(
        errorMessage: String,
        logMessage: String,
        position: Int
    ): KParseException {
      return KParseException(
          msg(errorMessage, logMessage, position, position + 1), logMessage)
    }

    /**
     * Creates a new parse exception for situations in which both the start and end positions of the
     * error are known.
     *
     * @param errorMessage the user error message.
     * @param logMessage the original log message.
     * @param start the index of the first character in the invalid section of the log message.
     * @param end the index after the last character in the invalid section of the log message.
     * @return the parser exception.
     */
    public fun withBounds(
        errorMessage: String,
        logMessage: String,
        start: Int,
        end: Int
    ): KParseException {
      return KParseException(
          msg(errorMessage, logMessage, start, end), logMessage)
    }
  }
}
