package com.buenaflor.kflogger.backend.system

import com.buenaflor.kflogger.KLevel
import com.buenaflor.kflogger.backend.KLogData
import com.buenaflor.kflogger.backend.KLoggerBackend
import com.buenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KBackendFactoryTest {
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

  private class CompileOnlyBackendFactory : KBackendFactory() {
    override fun create(loggingClassName: String): KLoggerBackend {
      return CompileOnlyLoggerBackend()
    }
  }

  @Test
  @IgnoreIos
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    CompileOnlyBackendFactory().create("test")
  }
}
