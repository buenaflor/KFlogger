package com.giancarlobuenaflor.kflogger.parser

import com.giancarlobuenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KMessageParserTest {
  private class CompileOnlyMessageParser : KMessageParser() {
    override fun <T> parseImpl(builder: KMessageBuilder<T>?) {}

    override fun unescape(out: StringBuilder?, message: String?, start: Int, end: Int) {}
  }

  @Test
  @IgnoreIos
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    val parser = CompileOnlyMessageParser()
    parser.unescape(null, null, 0, 0)
  }
}
