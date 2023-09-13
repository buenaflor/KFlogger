package com.giancarlobuenaflor.kflogger

import kotlin.test.Test
val logger = KFluentLogger.forEnclosingClass()

class KFluentLoggerTest {
  @Test
  fun testNotCrashing() {
    logger.atInfo().log("Hello, world! %s", "test")
    logger.atWarning().log("Hello, world!")
    logger.atSevere().log("Hello, world!")
    logger.atFine().log("Hello, world!")
    logger.atFiner().log("Hello, world!")
    logger.atFinest().log("Hello, world!")
    logger.atConfig().log("Hello, world!")
  }
}
