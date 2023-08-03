package com.buenaflor.kflogger.parser

import com.buenaflor.kflogger.backend.KFormatOptions
import com.buenaflor.kflogger.backend.KTemplateContext
import com.buenaflor.kflogger.backend.message
import com.buenaflor.kflogger.parameter.KParameter
import com.buenaflor.kflogger.parameter.KParameterVisitor
import com.buenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KMessageBuilderTest {
  private class CompileOnlyMessageParser : KMessageParser() {
    override fun <T> parseImpl(builder: KMessageBuilder<T>?) {}

    override fun unescape(out: StringBuilder?, message: String?, start: Int, end: Int) {}
  }

  private class CompileOnlyMessageBuilder<T>(val templateContext: KTemplateContext) :
      KMessageBuilder<String>(templateContext) {
    override fun addParameterImpl(termStart: Int, termEnd: Int, param: KParameter) {}

    override fun buildImpl(): String {
      return "${templateContext.message} + 1"
    }
  }

  private class CompileOnlyParameter(options: KFormatOptions, index: Int) :
      KParameter(options, index) {
    override fun accept(visitor: KParameterVisitor, value: Any) {}

    override fun getFormat(): String {
      return "format"
    }
  }

  @Test
  @IgnoreIos
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    val parser = CompileOnlyMessageParser()
    val templateContext = KTemplateContext(parser, "message")
    val messageBuilder = CompileOnlyMessageBuilder<String>(templateContext)
    val parameter = CompileOnlyParameter(KFormatOptions.getDefault(), 0)

    messageBuilder.addParameter(0, 0, parameter)
    messageBuilder.message
    messageBuilder.build()
    messageBuilder.parser
    messageBuilder.expectedArgumentCount
  }
}
