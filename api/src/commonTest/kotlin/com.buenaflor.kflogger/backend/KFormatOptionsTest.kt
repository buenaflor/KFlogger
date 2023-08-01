package com.buenaflor.kflogger.backend

import com.buenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KFormatOptionsTest {
  @Test
  @IgnoreIos
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    KFormatOptions.FLAG_PREFIX_SPACE_FOR_POSITIVE_VALUES
    KFormatOptions.FLAG_SHOW_ALT_FORM
    KFormatOptions.FLAG_USE_PARENS_FOR_NEGATIVE_VALUES
    KFormatOptions.FLAG_PREFIX_PLUS_FOR_POSITIVE_VALUES
    KFormatOptions.FLAG_SHOW_GROUPING
    KFormatOptions.FLAG_LEFT_ALIGN
    KFormatOptions.FLAG_SHOW_LEADING_ZEROS
    KFormatOptions.FLAG_UPPER_CASE
    KFormatOptions.ALL_FLAGS
    KFormatOptions.UNSET
    KFormatOptions.of(0, 1, 0)
    KFormatOptions.checkFlagConsistency(0, false)
    KFormatOptions.parse("", 0, 0, false)
    KFormatOptions.parseValidFlags("", false)
    KFormatOptions.getDefault()
    KFormatOptions.getDefault().isDefault()
    KFormatOptions.getDefault().areValidFor(KFormatChar.CHAR)
    KFormatOptions.getDefault().filter(0, false, false)
    KFormatOptions.getDefault().shouldLeftAlign()
    KFormatOptions.getDefault().shouldShowAltForm()
    KFormatOptions.getDefault().shouldShowGrouping()
    KFormatOptions.getDefault().shouldShowLeadingZeros()
    KFormatOptions.getDefault().shouldUpperCase()
    KFormatOptions.getDefault().shouldPrefixPlusForPositiveValues()
    KFormatOptions.getDefault().shouldPrefixSpaceForPositiveValues()
    KFormatOptions.getDefault().precision()
    KFormatOptions.getDefault().validate(0, false)
  }
}
