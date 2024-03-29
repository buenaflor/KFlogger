package com.giancarlobuenaflor.kflogger.backend

import com.giancarlobuenaflor.kflogger.parser.KMessageBuilder
import com.giancarlobuenaflor.kflogger.parser.KMessageParser
import com.giancarlobuenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KTemplateContextTest {
  private class CompileOnlyMessageParser : KMessageParser() {
    override fun <T> parseImpl(builder: KMessageBuilder<T>) {}

    override fun unescape(out: StringBuilder?, message: String?, start: Int, end: Int) {}
  }

  @Test
  @IgnoreIos
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    val parser = CompileOnlyMessageParser()
    val templateContext = KTemplateContext(parser, "message")
    templateContext.getMessage()
    templateContext.getParser()
  }
}
