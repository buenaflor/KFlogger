package com.buenaflor.kflogger.backend

import com.buenaflor.kflogger.*
import com.buenaflor.kflogger.parser.KMessageBuilder
import com.buenaflor.kflogger.parser.KMessageParser
import com.buenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KPlatformTest {
  private class CompileOnlyLogger(backend: KLoggerBackend) :
    KAbstractLogger<CompileOnlyLogger.Api>(backend) {
    interface Api : KLoggingApi<Api>

    override fun at(level: KLevel): Api {
      return Context(level, false)
    }

    /** Logging context implementing the fully specified API for this logger. */
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

      fun findLoggingClass(): String {
        return KPlatform.getCallerFinder().findLoggingClass(CompileOnlyLogger::class.toKlass())
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
    KPlatform.getCallerFinder().findLogSite(this::class.toKlass(), 0)
    KPlatform.getBackend("")
    KPlatform.getConfigInfo()
    KPlatform.getCurrentRecursionDepth()
    KPlatform.getCurrentTimeNanos()
    KPlatform.getInjectedTags()
    KPlatform.getInjectedMetadata()
    KPlatform.shouldForceLogging("", KLevel.INFO, true)
  }

  class Fao {
    companion object {
      fun log() {
        CompileOnlyLogger.findLoggingClass()
      }
    }
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
