package com.giancarlobuenaflor.kflogger.backend.system

import com.giancarlobuenaflor.kflogger.KLevel
import com.giancarlobuenaflor.kflogger.KLogRecord
import com.giancarlobuenaflor.kflogger.KLogSite
import com.giancarlobuenaflor.kflogger.backend.KLogData
import com.giancarlobuenaflor.kflogger.backend.KMetadata
import com.giancarlobuenaflor.kflogger.backend.KTemplateContext
import com.giancarlobuenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KAbstractBackendTest {
  private val compileOnlyBackend = CompileOnlyBackend()

  private class CompileOnlyBackend : KAbstractBackend("") {
    override fun log(data: KLogData) {}

    override fun handleError(error: RuntimeException, badData: KLogData) {}
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
    compileOnlyBackend.getLoggerName()
    compileOnlyBackend.isLoggable(KLevel.INFO)
  }
}
