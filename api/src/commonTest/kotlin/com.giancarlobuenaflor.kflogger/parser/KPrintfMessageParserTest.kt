package com.giancarlobuenaflor.kflogger.parser

import androidx.kruth.assertThat
import com.giancarlobuenaflor.kflogger.backend.KTemplateContext
import com.giancarlobuenaflor.kflogger.parameter.KParameter
import com.giancarlobuenaflor.kflogger.util.IgnoreIos
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
      return "${templateContext.getMessage()} + 1"
    }
  }

  @Test
  @IgnoreIos
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    messageParser.unescape(StringBuilder(), "", 0, 0)
  }

  @Test
  fun testPrintfNextTerm() {
    assertThat(KPrintfMessageParser.nextPrintfTerm("", 0)).isEqualTo(-1)
    assertThat(KPrintfMessageParser.nextPrintfTerm("%X", 0)).isEqualTo(0)
    assertThat(KPrintfMessageParser.nextPrintfTerm("Hello %X World %X", 0)).isEqualTo(6)
    assertThat(KPrintfMessageParser.nextPrintfTerm("Hello %X World %X", 6)).isEqualTo(6)
    assertThat(KPrintfMessageParser.nextPrintfTerm("Hello %X World %X", 7)).isEqualTo(15)
    assertThat(KPrintfMessageParser.nextPrintfTerm("Hello %% World %X", 0)).isEqualTo(15)
    assertThat(KPrintfMessageParser.nextPrintfTerm("Hello %X World %X", 16)).isEqualTo(-1)
  }

  @Test
  fun testUnescapePrintfSupportsNewline() {
    val nl = KPrintfMessageParser.getSafeSystemNewline()
    assertThat(unescapePrintf("%n")).isEqualTo(nl)
    assertThat(unescapePrintf("Hello %n World")).isEqualTo("Hello $nl World")
    assertThat(unescapePrintf("Hello World %n")).isEqualTo("Hello World $nl")
    assertThat(unescapePrintf("%n%n%%n%n")).isEqualTo("$nl$nl%n$nl")
  }

  @Test
  fun testUnescapePrintfIgnoresErrors() {
    assertThat(unescapePrintf("Hello % World")).isEqualTo("Hello % World")
    assertThat(unescapePrintf("Hello %")).isEqualTo("Hello %")
  }

  @Test
  fun testUnescapePrintf() {
    assertThat(unescapePrintf("")).isEqualTo("");
    assertThat(unescapePrintf("Hello World")).isEqualTo("Hello World");
    assertThat(unescapePrintf("Hello %% World")).isEqualTo("Hello % World");
    assertThat(unescapePrintf("Hello %%%% World")).isEqualTo("Hello %% World");
    assertThat(unescapePrintf("%% 'Hello {%%}{%%} World' %%"))
      .isEqualTo("% 'Hello {%}{%} World' %");
  }

  private companion object {
    fun unescapePrintf(message: String): String {
      val out = StringBuilder()
      KPrintfMessageParser.unescapePrintf(out, message, 0, message.length)
      return out.toString()
    }
  }
}
