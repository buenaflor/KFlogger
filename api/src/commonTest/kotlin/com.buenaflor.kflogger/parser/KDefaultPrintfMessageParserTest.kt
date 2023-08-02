package com.buenaflor.kflogger.parser

import com.buenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KDefaultPrintfMessageParserTest {
    @Test
    @IgnoreIos
    fun testNotCrashing() {
        // This test is to ensure that the code compiles and does not crash.
        val messageParser = KDefaultPrintfMessageParser.getInstance()
        messageParser.unescape(StringBuilder(), "", 0, 0)
    }
}