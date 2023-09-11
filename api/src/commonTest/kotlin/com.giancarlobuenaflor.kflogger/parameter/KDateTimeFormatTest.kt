package com.giancarlobuenaflor.kflogger.parameter

import com.giancarlobuenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KDateTimeFormatTest {
  @Test
  @IgnoreIos
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    KDateTimeFormat.TIME_HOUR_OF_DAY_PADDED.char
    KDateTimeFormat.TIME_HOUR_OF_DAY.char
    KDateTimeFormat.TIME_HOUR_12H_PADDED.char
    KDateTimeFormat.TIME_HOUR_12H.char
    KDateTimeFormat.TIME_MINUTE_OF_HOUR_PADDED.char
    KDateTimeFormat.TIME_SECONDS_OF_MINUTE_PADDED.char
    KDateTimeFormat.TIME_MILLIS_OF_SECOND_PADDED.char
    KDateTimeFormat.TIME_NANOS_OF_SECOND_PADDED.char
    KDateTimeFormat.TIME_AM_PM.char
    KDateTimeFormat.TIME_TZ_NUMERIC.char
    KDateTimeFormat.TIME_TZ_SHORT.char
    KDateTimeFormat.TIME_EPOCH_SECONDS.char
    KDateTimeFormat.TIME_EPOCH_MILLIS.char
    KDateTimeFormat.DATE_MONTH_FULL.char
    KDateTimeFormat.DATE_MONTH_SHORT.char
    KDateTimeFormat.DATE_MONTH_SHORT_ALT.char
    KDateTimeFormat.DATE_DAY_FULL.char
    KDateTimeFormat.DATE_DAY_SHORT.char
    KDateTimeFormat.DATE_CENTURY_PADDED.char
    KDateTimeFormat.DATE_YEAR_PADDED.char
    KDateTimeFormat.DATE_YEAR_OF_CENTURY_PADDED.char
    KDateTimeFormat.DATE_DAY_OF_YEAR_PADDED.char
    KDateTimeFormat.DATE_MONTH_PADDED.char
    KDateTimeFormat.DATE_DAY_OF_MONTH_PADDED.char
    KDateTimeFormat.DATE_DAY_OF_MONTH.char
    KDateTimeFormat.DATETIME_HOURS_MINUTES.char
    KDateTimeFormat.DATETIME_HOURS_MINUTES_SECONDS.char
    KDateTimeFormat.DATETIME_HOURS_MINUTES_SECONDS_12H.char
    KDateTimeFormat.DATETIME_MONTH_DAY_YEAR.char
    KDateTimeFormat.DATETIME_YEAR_MONTH_DAY.char
    KDateTimeFormat.DATETIME_FULL.char

    KDateTimeFormat.of('H')
    KDateTimeFormat.of('k')
    KDateTimeFormat.of('I')
    KDateTimeFormat.of('l')
    KDateTimeFormat.of('M')
    KDateTimeFormat.of('S')
    KDateTimeFormat.of('L')
    KDateTimeFormat.of('N')
    KDateTimeFormat.of('p')
    KDateTimeFormat.of('z')
    KDateTimeFormat.of('Z')
    KDateTimeFormat.of('s')
    KDateTimeFormat.of('Q')
    KDateTimeFormat.of('B')
    KDateTimeFormat.of('b')
    KDateTimeFormat.of('h')
    KDateTimeFormat.of('A')
    KDateTimeFormat.of('a')
    KDateTimeFormat.of('C')
    KDateTimeFormat.of('Y')
    KDateTimeFormat.of('y')
    KDateTimeFormat.of('j')
    KDateTimeFormat.of('m')
    KDateTimeFormat.of('d')
    KDateTimeFormat.of('e')
    KDateTimeFormat.of('R')
    KDateTimeFormat.of('T')
    KDateTimeFormat.of('r')
    KDateTimeFormat.of('D')
    KDateTimeFormat.of('F')
    KDateTimeFormat.of('c')
  }
}
