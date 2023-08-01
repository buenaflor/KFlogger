package com.buenaflor.kflogger.parser

/**
 * Default implementation of the printf message parser. This parser supports all the place-holders
 * available in `String#format` but can be extended, if desired, for additional behavior
 * For consistency it is recommended, but not required, that custom printf parsers always extend
 * from this class.
 *
 *
 * This class is immutable and thread safe (and any subclasses must also be so).
 */
public actual class KDefaultPrintfMessageParser private actual constructor() : KPrintfMessageParser() {
    actual override fun parsePrintfTerm(
        builder: KMessageBuilder<*>?,
        index: Int,
        message: String?,
        termStart: Int,
        specStart: Int,
        formatStart: Int
    ): Int {
        TODO()
    }

    public actual companion object {
        public actual fun getInstance(): KPrintfMessageParser? {
            TODO()
        }
    }
}
