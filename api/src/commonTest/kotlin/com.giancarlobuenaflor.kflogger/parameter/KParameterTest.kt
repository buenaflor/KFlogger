package com.giancarlobuenaflor.kflogger.parameter

import com.giancarlobuenaflor.kflogger.backend.KFormatOptions
import com.giancarlobuenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KParameterTest {
  private class CompileOnlyParameter : KParameter(KFormatOptions.getDefault(), 1) {
    override fun accept(visitor: KParameterVisitor, value: Any) {}

    override fun getFormat(): String {
      return "format"
    }
  }

  @Test
  @IgnoreIos
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    val parameter = CompileOnlyParameter()
    parameter.getFormat()
    parameter.getIndex()
  }
}
