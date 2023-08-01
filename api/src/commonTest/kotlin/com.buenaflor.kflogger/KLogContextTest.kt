package com.buenaflor.kflogger

import com.buenaflor.kflogger.backend.KLogData
import com.buenaflor.kflogger.backend.KLoggerBackend
import com.buenaflor.kflogger.backend.metadata
import com.buenaflor.kflogger.parser.KMessageBuilder
import com.buenaflor.kflogger.parser.KMessageParser
import com.buenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KLogContextTest {
    // Note: CompileOnly classes do not represent correct functionality but only deal with testing the compilation.

    abstract class CompileOnlyLogContext<LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> : KLogContext<LOGGER, API>(KLevel.WARNING, false)

    class CompileOnlyBackend : KLoggerBackend() {
        override fun isLoggable(lvl: KLevel): Boolean {
            return true
        }

        override fun log(data: KLogData?) {
            
        }

        override fun handleError(error: RuntimeException, badData: KLogData) {
            
        }

        override fun getLoggerName(): String {
            return ""
        }
    }

    class CompileOnlyLogger : KAbstractLogger<CompileOnlyLogger.Api>(CompileOnlyBackend()) {
        interface Api : KLoggingApi<Api>

        class NoOp : KLoggingApiNoOp<Api>(), Api

        override fun at(level: KLevel?): Api {
            return NoOp()
        }
    }

    class CompileOnlyMessageParser : KMessageParser() {
        override fun <T> parseImpl(builder: KMessageBuilder<T>?) {

        }

        override fun unescape(out: StringBuilder?, message: String?, start: Int, end: Int) {

        }
    }

    class CompileOnlyContext : CompileOnlyLogContext<CompileOnlyLogger, CompileOnlyLogger.Api>() {
        override fun api(): CompileOnlyLogger.Api {
            return CompileOnlyLogger().atFine()
        }

        override fun getLogger(): CompileOnlyLogger {
            return CompileOnlyLogger()
        }

        override fun noOp(): CompileOnlyLogger.Api {
            return CompileOnlyLogger().atFine()
        }

        override fun getMessageParser(): KMessageParser {
            return CompileOnlyMessageParser()
        }

    }

    class CompileOnlyLoggingScopeProvider : KLoggingScopeProvider {
        override fun getCurrentScope(): KLoggingScope? {
            return null
        }
    }

    class CompileOnlyLogPerBucketingStrategy : KLogPerBucketingStrategy<String>("") {
        override fun apply(key: String): Any? {
            return null
        }
    }

    class CompileOnlyLogSite : KLogSite() {
        override fun getClassName(): String {
            return ""
        }

        override fun getMethodName(): String {
            return ""
        }

        override fun getLineNumber(): Int {
            return 0
        }

        override fun getFileName(): String {
            return ""
        }
    }

    @Test
    @IgnoreIos
    fun testNotCrashing() {
        // This test is to ensure that the code compiles and does not crash.
        val context = CompileOnlyContext()
        context.logVarargs("", arrayOf("", 0))
        context.log()
        context.log("")
        context.log("", 0)
        context.log("", 0, 0)
        context.log("", 0, 0, 0)
        context.log("", 0, 0, 0, 0)
        context.log("", 0, 0, 0, 0, 0)
        context.log("", 0, 0, 0, 0, 0, 0)
        context.log("", 0, 0, 0, 0, 0, 0, 0)
        context.log("", 0, 0, 0, 0, 0, 0, 0, 0)
        context.log("", 0, 0, 0, 0, 0, 0, 0, 0, 0)
        context.log("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        context.log("", 0.toChar())
        context.log("", 0.toByte())
        context.log("", 0.toShort())
        context.log("", 0)
        context.log("", 0L)
        context.log("", 0, true)
        context.log("", 0.toChar(), true)
        context.log("", 0.toByte(), true)
        context.log("", 0.toShort(), true)
        context.log("", 0, true)
        context.log("", 0L, true)
        context.log("", 0F, true)
        context.log("", 0.0, true)
        context.log("", true, 0)
        context.log("", true, 0.toChar())
        context.log("", true, 0.toByte())
        context.log("", true, 0.toShort())
        context.log("", true, 0)
        context.log("", true, 0L)
        context.log("", true, 0F)
        context.log("", true, 0.0)
        context.log("", true, true)
        context.log("", true, 0.toChar())
        context.log("", true, 0.toByte())
        context.log("", true, 0.toShort())
        context.log("", true, 0)
        context.log("", true, 0L)
        context.log("", true, 0F)
        context.log("", true, 0.0)
        context.log("", true, 'a')
        context.log("", 'a', 'a')
        context.log("", 'a', 0.toByte())
        context.log("", 'a', 0.toShort())
        context.log("", 'a', 0)
        context.log("", 'a', 0L)
        context.log("", 'a', 0F)
        context.log("", 'a', 0.0)
        context.log("", true, 0.toByte())
        context.log("", 'a', 0.toByte())
        context.log("", 0.toByte(), 0.toByte())
        context.log("", 0.toShort(), 0.toByte())
        context.log("", 0, 0.toByte())
        context.log("", 0L, 0.toByte())
        context.log("", 0F, 0.toByte())
        context.log("", 0.0, 0.toByte())
        context.log("", true, 0.toShort())
        context.log("", 'a', 0.toShort())
        context.log("", 0.toByte(), 0.toShort())
        context.log("", 0.toShort(), 0.toShort())
        context.log("", 0, 0.toShort())
        context.log("", 0L, 0.toShort())
        context.log("", 0F, 0.toShort())
        context.log("", 0.0, 0.toShort())
        context.log("", true, 0)
        context.log("", 'a', 0)
        context.log("", 0.toByte(), 0)
        context.log("", 0.toShort(), 0)
        context.log("", 0, 0)
        context.log("", 0L, 0)
        context.log("", 0F, 0)
        context.log("", 0.0, 0)
        context.log("", true, 0L)
        context.log("", 'a', 0L)
        context.log("", 0.toByte(), 0L)
        context.log("", 0.toShort(), 0L)
        context.log("", 0, 0L)
        context.log("", 0L, 0L)
        context.log("", 0F, 0L)
        context.log("", 0.0, 0L)
        context.log("", true, 0F)
        context.log("", 'a', 0F)
        context.log("", 0.toByte(), 0F)
        context.log("", 0.toShort(), 0F)
        context.log("", 0, 0F)
        context.log("", 0L, 0F)
        context.log("", 0F, 0F)
        context.log("", 0.0, 0F)
        context.log("", true, 0.0)
        context.log("", 'a', 0.0)
        context.log("", 0.toByte(), 0.0)
        context.log("", 0.toShort(), 0.0)
        context.log("", 0, 0.0)
        context.log("", 0L, 0.0)
        context.log("", 0F, 0.0)
        context.log("", 0.0, 0.0)
        context.atMostEvery(1, KTimeUnit.SECONDS)
        context.every(1)
        context.isEnabled()
        context.onAverageEvery(1)
        context.per(KTimeUnit.SECONDS)
        context.per(CompileOnlyLoggingScopeProvider())
        context.per("", CompileOnlyLogPerBucketingStrategy())
        context.wasForced()
        context.with(KLogContextKey.WAS_FORCED, true)
        context.withCause(Throwable())
        context.withInjectedLogSite(CompileOnlyLogSite())
        context.withInjectedLogSite("", "", 0, "")
        context.withStackTrace(KStackSize.FULL)
        context.arguments
        context.level
        context.logSite
        context.loggerName
        context.metadata
        context.timestampNanos
        context.timestampMicros

        KLogContextKey.LOG_CAUSE
        KLogContextKey.LOG_EVERY_N
        KLogContextKey.CONTEXT_STACK_SIZE
        KLogContextKey.LOG_SAMPLE_EVERY_N
        KLogContextKey.LOG_SITE_GROUPING_KEY
        KLogContextKey.SKIPPED_LOG_COUNT
        KLogContextKey.WAS_FORCED
    }
}