package com.buenaflor.kflogger.parameter

import com.buenaflor.kflogger.backend.KFormatOptions
import com.buenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KParameterTest {
  class CustomParameter : KParameter(KFormatOptions.getDefault(), 1) {
    override fun accept(visitor: KParameterVisitor, value: Any) {}

    override fun getFormat(): String {
      formatOptions
      return "format"
    }
  }

  @Test
  @IgnoreIos
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    val parameter = CustomParameter()
    parameter.getFormat()
    parameter.index()
  }
}
