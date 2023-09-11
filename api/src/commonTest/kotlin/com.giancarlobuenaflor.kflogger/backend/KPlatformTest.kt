package com.giancarlobuenaflor.kflogger.backend

import com.giancarlobuenaflor.kflogger.*
import com.giancarlobuenaflor.kflogger.parser.KMessageBuilder
import com.giancarlobuenaflor.kflogger.parser.KMessageParser
import com.giancarlobuenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KPlatformTest {
  private class CompileOnlyLogger(backend: KLoggerBackend) :
      KAbstractLogger<CompileOnlyLogger.Api>(backend) {
    interface Api : KLoggingApi<Api>

    override fun at(level: KLevel): Api {
      return Context(level, false)
    }

    inner class Context internal constructor(level: KLevel, isForced: Boolean) :
        KLogContext<CompileOnlyLogger, Api>(level, isForced), Api {

      override fun getLogger(): CompileOnlyLogger {
        return this@CompileOnlyLogger
      }

      override fun noOp(): Api {
        return this@CompileOnlyLogger.atFine()
      }

      override fun getMessageParser(): KMessageParser {
        return CompileOnlyMessageParser()
      }

      override fun api(): Api {
        return this
      }
    }

    companion object {
      /**
       * Returns a new logger instance which parses log messages using printf format for the
       * enclosing class using the system default logging backend.
       */
      fun forEnclosingClass(): CompileOnlyLogger {
        val loggingClass: String =
            KPlatform.getCallerFinder().findLoggingClass(CompileOnlyLogger::class.toKlass())
        return CompileOnlyLogger(KPlatform.getBackend(loggingClass))
      }
    }
  }

  private class CompileOnlyMessageParser : KMessageParser() {
    override fun <T> parseImpl(builder: KMessageBuilder<T>?) {}

    override fun unescape(out: StringBuilder?, message: String?, start: Int, end: Int) {}
  }

  @Test
  @IgnoreIos
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    CompileOnlyLogger.forEnclosingClass() // test findLoggingClass
    KFluentLogger.forEnclosingClass().atWarning().log("hello %s", 1)
    KPlatform.getCallerFinder().findLogSite(this::class.toKlass(), 0)
    KPlatform.getBackend("")
    KPlatform.getConfigInfo()
    KPlatform.getCurrentRecursionDepth()
    KPlatform.getCurrentTimeNanos()
    KPlatform.getInjectedTags()
    KPlatform.getInjectedMetadata()
    KPlatform.shouldForceLogging("", KLevel.INFO, true)
  }

  @Test
  fun testFindLoggingClass() {
    val logger = CompileOnlyLogger.forEnclosingClass()
    logger.atWarning().log("What's up")
    logger.atFine().log("What's up2")
    logger.atInfo().log("What's up3")
    logger.atSevere().log("What's up4")
    logger.atFiner().log("What's up5")
    logger.atFinest().log("What's up6")
  }
}
