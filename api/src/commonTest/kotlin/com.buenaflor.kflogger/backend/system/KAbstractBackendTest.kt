package com.buenaflor.kflogger.backend.system

import com.buenaflor.kflogger.KLevel
import com.buenaflor.kflogger.KLogRecord
import com.buenaflor.kflogger.KLogSite
import com.buenaflor.kflogger.backend.KLogData
import com.buenaflor.kflogger.backend.KMetadata
import com.buenaflor.kflogger.backend.KTemplateContext
import com.buenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KAbstractBackendTest {
  private val compileOnlyBackend = CompileOnlyBackend()

  private class CompileOnlyBackend : KAbstractBackend("") {
    override fun log(data: KLogData?) {}

    override fun handleError(error: RuntimeException, badData: KLogData) {}

    override fun getLoggerName(): String {
      return ""
    }
  }

  private class CompileOnlyLogData : KLogData {
    override fun wasForced(): Boolean {
      return false
    }

    override fun getLevel(): KLevel {
      return KLevel.WARNING
    }

    override fun getMetadata(): KMetadata? {
      return null
    }

    override fun getTimestampMicros(): Long {
      return 0
    }

    override fun getTimestampNanos(): Long {
      return 0
    }

    override fun getLoggerName(): String? {
      return null
    }

    override fun getLogSite(): KLogSite? {
      return null
    }

    override fun getArguments(): Array<Any?>? {
      return null
    }

    override fun getLiteralArgument(): Any? {
      return null
    }

    override fun getTemplateContext(): KTemplateContext? {
      return null
    }
  }

  @Test
  @IgnoreIos
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    compileOnlyBackend.handleError(RuntimeException(), CompileOnlyLogData())
    compileOnlyBackend.log(CompileOnlyLogData())
    compileOnlyBackend.log(KLogRecord(KLevel.INFO, "test"), false)
    compileOnlyBackend.loggerName
    compileOnlyBackend.isLoggable(KLevel.INFO)
  }
}
