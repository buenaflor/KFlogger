package com.buenaflor.kflogger.backend.system

import com.buenaflor.kflogger.KAbstractLogger
import com.buenaflor.kflogger.KLevel
import com.buenaflor.kflogger.KLogSite
import com.buenaflor.kflogger.Klass
import com.buenaflor.kflogger.backend.KLogData
import com.buenaflor.kflogger.backend.KLoggerBackend
import com.buenaflor.kflogger.backend.KPlatformLogCallerFinder
import com.buenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KDefaultPlatformTest {
  private class CompileOnlyLoggerBackend : KLoggerBackend() {
    override fun isLoggable(level: KLevel): Boolean {
      return false
    }

    override fun log(data: KLogData) {}

    override fun handleError(error: RuntimeException, badData: KLogData) {}

    override fun getLoggerName(): String {
      return ""
    }
  }

  private class CompileOnlyLogCallerFinder : KPlatformLogCallerFinder() {
    override fun findLoggingClass(loggerClass: Klass<out KAbstractLogger<*>>): String {
      return ""
    }

    override fun findLogSite(loggerApi: Klass<*>, stackFramesToSkip: Int): KLogSite {
      return KLogSite.INVALID
    }
  }

  private class CompileOnlyDefaultPlatform : KDefaultPlatform() {
    override fun getCallerFinderImpl(): KPlatformLogCallerFinder {
      return CompileOnlyLogCallerFinder()
    }

    override fun getBackendImpl(className: String?): KLoggerBackend {
      return CompileOnlyLoggerBackend()
    }

    override fun getConfigInfoImpl(): String {
      return ""
    }

    override fun getCurrentTimeNanosImpl(): Long {
      return 0L
    }

    fun compile() {
      getCallerFinderImpl()
      getBackendImpl(null)
      getConfigInfoImpl()
      getCurrentTimeNanosImpl()
    }
  }

  @Test
  @IgnoreIos
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    CompileOnlyDefaultPlatform().compile()
  }
}
