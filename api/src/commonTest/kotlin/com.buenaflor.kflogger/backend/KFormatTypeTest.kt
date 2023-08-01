package com.buenaflor.kflogger.backend

import androidx.kruth.assertThat
import com.buenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KFormatTypeTest {
  @Test
  @IgnoreIos
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    KFormatType.GENERAL
    KFormatType.BOOLEAN
    KFormatType.CHARACTER
    KFormatType.INTEGRAL
    KFormatType.FLOAT

    KFormatType.GENERAL.canFormat(null)
    KFormatType.BOOLEAN.canFormat(null)
    KFormatType.CHARACTER.canFormat(null)
    KFormatType.INTEGRAL.canFormat(null)
    KFormatType.FLOAT.canFormat(null)

    KFormatType.GENERAL.isNumeric()
    KFormatType.BOOLEAN.isNumeric()
    KFormatType.CHARACTER.isNumeric()
    KFormatType.INTEGRAL.isNumeric()
    KFormatType.FLOAT.isNumeric()

    KFormatType.GENERAL.supportsPrecision()
    KFormatType.BOOLEAN.supportsPrecision()
    KFormatType.CHARACTER.supportsPrecision()
    KFormatType.INTEGRAL.supportsPrecision()
    KFormatType.FLOAT.supportsPrecision()
  }
}
