package com.buenaflor.kflogger.parser

import com.buenaflor.kflogger.backend.KTemplateContext
import com.buenaflor.kflogger.backend.message
import com.buenaflor.kflogger.parameter.KParameter
import com.buenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KPrintfMessageParserTest {
    private val messageParser = CompileOnlyPrintfMessageParser()
    private val templateContext = KTemplateContext(messageParser, "message")
    private val messageBuilder = CompileOnlyMessageBuilder<String>(templateContext)

    private class CompileOnlyPrintfMessageParser : KPrintfMessageParser() {
        override fun parsePrintfTerm(
            builder: KMessageBuilder<*>?,
            index: Int,
            message: String?,
            termStart: Int,
            specStart: Int,
            formatStart: Int
        ): Int {
            return 1
        }
    }

    private class CompileOnlyMessageBuilder<T>(val templateContext: KTemplateContext) :
        KMessageBuilder<String>(templateContext) {
        override fun addParameterImpl(termStart: Int, termEnd: Int, param: KParameter) {}

        override fun buildImpl(): String {
            return "${templateContext.message} + 1"
        }
    }

    @Test
    @IgnoreIos
    fun testNotCrashing() {
        // This test is to ensure that the code compiles and does not crash.
        messageParser.unescape(StringBuilder(), "", 0, 0)
    }
}