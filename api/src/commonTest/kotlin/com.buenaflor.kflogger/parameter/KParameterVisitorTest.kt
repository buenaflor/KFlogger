package com.buenaflor.kflogger.parameter

import com.buenaflor.kflogger.backend.KFormatChar
import com.buenaflor.kflogger.backend.KFormatOptions
import com.buenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KParameterVisitorTest {
  class CustomParameterVisitor : KParameterVisitor {
    override fun visit(value: Any?, format: KFormatChar?, options: KFormatOptions?) {}

    override fun visitDateTime(value: Any?, format: KDateTimeFormat?, options: KFormatOptions?) {}

    override fun visitPreformatted(value: Any?, formatted: String?) {}

    override fun visitMissing() {}

    override fun visitNull() {}
  }

  @Test
  @IgnoreIos
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    val visitor = CustomParameterVisitor()
    visitor.visit(null, null, null)
    visitor.visitDateTime(null, null, null)
    visitor.visitPreformatted(null, null)
    visitor.visitMissing()
    visitor.visitNull()
  }
}
