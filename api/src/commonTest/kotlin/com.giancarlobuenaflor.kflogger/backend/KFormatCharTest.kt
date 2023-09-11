package com.giancarlobuenaflor.kflogger.backend

import com.giancarlobuenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KFormatCharTest {
  @Test
  @IgnoreIos
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    KFormatChar.STRING
    KFormatChar.BOOLEAN
    KFormatChar.CHAR
    KFormatChar.DECIMAL
    KFormatChar.OCTAL
    KFormatChar.HEX
    KFormatChar.FLOAT
    KFormatChar.EXPONENT
    KFormatChar.GENERAL
    KFormatChar.EXPONENT_HEX

    KFormatChar.STRING.allowedFlags
    KFormatChar.BOOLEAN.allowedFlags
    KFormatChar.CHAR.allowedFlags
    KFormatChar.DECIMAL.allowedFlags
    KFormatChar.OCTAL.allowedFlags
    KFormatChar.HEX.allowedFlags
    KFormatChar.FLOAT.allowedFlags
    KFormatChar.EXPONENT.allowedFlags
    KFormatChar.GENERAL.allowedFlags
    KFormatChar.EXPONENT_HEX.allowedFlags

    KFormatChar.STRING.type
    KFormatChar.BOOLEAN.type
    KFormatChar.CHAR.type
    KFormatChar.DECIMAL.type
    KFormatChar.OCTAL.type
    KFormatChar.HEX.type
    KFormatChar.FLOAT.type
    KFormatChar.EXPONENT.type
    KFormatChar.GENERAL.type
    KFormatChar.EXPONENT_HEX.type

    KFormatChar.STRING.defaultFormatString
    KFormatChar.BOOLEAN.defaultFormatString
    KFormatChar.CHAR.defaultFormatString
    KFormatChar.DECIMAL.defaultFormatString
    KFormatChar.OCTAL.defaultFormatString
    KFormatChar.HEX.defaultFormatString
    KFormatChar.FLOAT.defaultFormatString
    KFormatChar.EXPONENT.defaultFormatString
    KFormatChar.GENERAL.defaultFormatString
    KFormatChar.EXPONENT_HEX.defaultFormatString

    KFormatChar.of('a')
  }
}
