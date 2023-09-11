package com.giancarlobuenaflor.kflogger.parameter

import com.giancarlobuenaflor.kflogger.backend.KFormatChar
import com.giancarlobuenaflor.kflogger.backend.KFormatOptions
import com.giancarlobuenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KParameterVisitorTest {
  private class CompileOnlyParameterVisitor : KParameterVisitor {
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
    val visitor = CompileOnlyParameterVisitor()
    visitor.visit(null, null, null)
    visitor.visitDateTime(null, null, null)
    visitor.visitPreformatted(null, null)
    visitor.visitMissing()
    visitor.visitNull()
  }
}
