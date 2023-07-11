package com.buenaflor.kflogger

public expect enum class TimeUnit {
  /** Time unit representing one thousandth of a microsecond. */
  NANOSECONDS,

  /** Time unit representing one thousandth of a millisecond. */
  MICROSECONDS,

  /** Time unit representing one thousandth of a second. */
  MILLISECONDS,

  /** Time unit representing one second. */
  SECONDS,

  /** Time unit representing sixty seconds. */
  MINUTES,

  /** Time unit representing sixty minutes. */
  HOURS,

  /** Time unit representing twenty four hours. */
  DAYS
}
