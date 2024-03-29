package com.giancarlobuenaflor.kflogger.parser

import com.giancarlobuenaflor.kflogger.backend.KFormatOptions
import com.giancarlobuenaflor.kflogger.parameter.KParameter

/**
 * Default implementation of the printf message parser. This parser supports all the place-holders
 * available in `String#format` but can be extended, if desired, for additional behavior For
 * consistency it is recommended, but not required, that custom printf parsers always extend from
 * this class.
 *
 * This class is immutable and thread safe (and any subclasses must also be so).
 */
public actual class KDefaultPrintfMessageParser private actual constructor() :
    KPrintfMessageParser() {

  actual override fun parsePrintfTerm(
      builder: KMessageBuilder<*>?,
      index: Int,
      message: String?,
      termStart: Int,
      specStart: Int,
      formatStart: Int
  ): Int {
    TODO("Not yet implemented")
  }

  public fun pubParsePrintfTerm(
      builder: KMessageBuilder<*>?,
      index: Int,
      message: String?,
      termStart: Int,
      specStart: Int,
      formatStart: Int,
  ): Int {
    return parsePrintfTerm(builder, index, message, termStart, specStart, formatStart)
  }

  public actual companion object {
    private val INSTANCE: KPrintfMessageParser = KDefaultPrintfMessageParser()

    public actual fun getInstance(): KPrintfMessageParser {
      return INSTANCE
    }

    private fun wrapHexParameter(options: KFormatOptions, index: Int): KParameter {
      TODO("Not yet implemented")
    }
  }
}
