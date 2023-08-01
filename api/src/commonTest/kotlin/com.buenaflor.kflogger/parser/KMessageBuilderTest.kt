package com.buenaflor.kflogger.parser

import androidx.kruth.assertThat
import com.buenaflor.kflogger.backend.KFormatOptions
import com.buenaflor.kflogger.backend.KTemplateContext
import com.buenaflor.kflogger.backend.message
import com.buenaflor.kflogger.parameter.KParameter
import com.buenaflor.kflogger.parameter.KParameterVisitor
import kotlin.test.Test

class KMessageBuilderTest {
  val parser = TestKMessageParser()
  val templateContext = KTemplateContext(parser, "message")
  val messageBuilder = TestKMessageBuilder<String>(templateContext)
  val parameter = TestKParameter(KFormatOptions.getDefault(), 0)

  class TestKMessageParser : KMessageParser() {
    override fun <T> parseImpl(builder: KMessageBuilder<T>?) {}

    override fun unescape(out: StringBuilder?, message: String?, start: Int, end: Int) {}
  }

  class TestKMessageBuilder<T>(val templateContext: KTemplateContext) :
      KMessageBuilder<String>(templateContext) {
    override fun addParameterImpl(termStart: Int, termEnd: Int, param: KParameter) {}

    override fun buildImpl(): String {
      return "${templateContext.message} + 1"
    }
  }

  class TestKParameter(options: KFormatOptions, index: Int) : KParameter(options, index) {
    override fun accept(visitor: KParameterVisitor, value: Any) {}

    override fun getFormat(): String {
      return "format"
    }
  }

  @Test
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    messageBuilder.addParameter(0, 0, parameter)
    assertThat(messageBuilder.message).isEqualTo(templateContext.message)
    assertThat(messageBuilder.build()).isEqualTo("${templateContext.message} + 1")
    assertThat(messageBuilder.parser).isEqualTo(parser)
    assertThat(messageBuilder.expectedArgumentCount).isEqualTo(1)
  }
}
