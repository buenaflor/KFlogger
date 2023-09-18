package com.giancarlobuenaflor.kflogger

import androidx.kruth.assertThat
import com.giancarlobuenaflor.kflogger.backend.KLogData
import com.giancarlobuenaflor.kflogger.backend.KLoggerBackend
import com.giancarlobuenaflor.kflogger.parser.KMessageBuilder
import com.giancarlobuenaflor.kflogger.parser.KMessageParser
import com.giancarlobuenaflor.kflogger.testing.KFakeLoggerBackend
import com.giancarlobuenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test
import kotlin.test.fail

class KLogContextTest {
  // Note: CompileOnly classes do not represent correct functionality but only deal with testing the
  // compilation.

  abstract class CompileOnlyLogContext<LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> :
      KLogContext<LOGGER, API>(KLevel.WARNING, false)

  class CompileOnlyBackend : KLoggerBackend() {
    override fun isLoggable(level: KLevel): Boolean {
      return true
    }

    override fun log(data: KLogData) {}

    override fun handleError(error: RuntimeException, badData: KLogData) {}

    override fun getLoggerName(): String {
      return ""
    }
  }

  class CompileOnlyLogger : KAbstractLogger<CompileOnlyLogger.Api>(CompileOnlyBackend()) {
    interface Api : KLoggingApi<Api>

    class NoOp : KLoggingApiNoOp<Api>(), Api

    override fun at(level: KLevel): Api {
      return NoOp()
    }
  }

  class CompileOnlyMessageParser : KMessageParser() {
    override fun <T> parseImpl(builder: KMessageBuilder<T>) {}

    override fun unescape(out: StringBuilder?, message: String?, start: Int, end: Int) {}
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

  // Arbitrary constants of overloaded types for testing argument mappings.
  private val BYTE_ARG = Byte.MAX_VALUE
  private val SHORT_ARG = Short.MAX_VALUE
  private val INT_ARG = Int.MAX_VALUE
  private val LONG_ARG = Long.MAX_VALUE
  private val CHAR_ARG = 'X'
  private val OBJECT_ARG = Any()

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
    context.getArguments()
    context.getLevel()
    context.getLogSite()
    context.getLoggerName()
    context.getMetadata()
    context.getTimestampNanos()
    context.getTimestampMicros()

    KLogContextKey.LOG_CAUSE
    KLogContextKey.LOG_EVERY_N
    KLogContextKey.CONTEXT_STACK_SIZE
    KLogContextKey.LOG_SAMPLE_EVERY_N
    KLogContextKey.LOG_SITE_GROUPING_KEY
    KLogContextKey.SKIPPED_LOG_COUNT
    KLogContextKey.WAS_FORCED
  }

  @Test
  fun testIsEnabled() {
    val backend = KFakeLoggerBackend()
    val logger = KFluentLogger(backend)
    backend.setLevel(KLevel.INFO)
    assertThat(logger.atFine().isEnabled()).isFalse()
    assertThat(logger.atInfo().isEnabled()).isTrue()
    assertThat(logger.atWarning().isEnabled()).isTrue()
  }

  @Test
  fun testNoArguments() {
    val backend = KFakeLoggerBackend()
    val logger = KFluentLogger(backend)
    logger.atInfo().log()
    backend.assertLastLogged().hasMessage("")
    backend.assertLastLogged().hasArguments()
  }

  @Test
  fun testNullLiteral() {
    val backend = KFakeLoggerBackend()
    val logger = KFluentLogger(backend)
    // We want to call log(String) (not log(Object)) with a null value.
    logger.atInfo().log(null as String?)
    backend.assertLastLogged().hasMessage(null)
  }

  @Test
  fun testLiteralArgument_doesNotEscapePercent() {
    val backend = KFakeLoggerBackend()
    val logger = KFluentLogger(backend)
    logger.atInfo().log("Hello %s World")
    backend.assertLastLogged().hasMessage("Hello %s World")
    backend.assertLastLogged().hasArguments()
  }

  @Test
  fun testLiteralMessage() {
    val backend = KFakeLoggerBackend()
    val logger = KFluentLogger(backend)
    logger.atInfo().log("Literal Message")
    assertThat(backend.loggedCount).isEqualTo(1)
    backend.assertLastLogged().hasMessage("Literal Message")

    // Cannot ask for format arguments as none exist.
    assertThat(backend.getLogged(0).getTemplateContext()).isNull()
    try {
      backend.getLogged(0).getArguments()
      fail("expected IllegalStateException")
    } catch (expected: IllegalStateException) {}
  }

  // Tests that null arguments are passed unmodified to the backend without throwing an exception.
  @Test
  fun testNullArgument() {
    val backend = KFakeLoggerBackend()
    val logger = KFluentLogger(backend)
    logger.atInfo().log("Hello %d World", null)
    backend.assertLastLogged().hasMessage("Hello %d World")
    backend.assertLastLogged().hasArguments(*arrayOf(null))
  }

  // Currently having a null message and a null argument will throw a runtime exception, but
  // perhaps it shouldn't (it could come from data). In general it is expected that when there are
  // arguments to a log statement the message is a literal, which makes this situation very
  // unlikely and probably a code bug (but even then throwing an exception is something that will
  // only happen when the log statement is enabled).
  // TODO(dbeaumont): Consider allowing this case to work without throwing a runtime exception.
  @Test
  fun testNullMessageAndArgument() {
    val backend = KFakeLoggerBackend()
    val logger = KFluentLogger(backend)
    try {
      logger.atInfo().log(null, null)
      fail("null message and arguments should fail")
    } catch (expected: NullPointerException) {}
  }

  @Test
  fun testManyObjectParameters() {
    val backend = KFakeLoggerBackend()
    val logger = KFluentLogger(backend)
    val ms = "Any message will do..."

    // Verify that the arguments passed in to the Object based methods are mapped correctly.
    logger.atInfo().log(ms, "1")
    backend.assertLastLogged().hasArguments("1")
    logger.atInfo().log(ms, "1", "2")
    backend.assertLastLogged().hasArguments("1", "2")
    logger.atInfo().log(ms, "1", "2", "3")
    backend.assertLastLogged().hasArguments("1", "2", "3")
    logger.atInfo().log(ms, "1", "2", "3", "4")
    backend.assertLastLogged().hasArguments("1", "2", "3", "4")
    logger.atInfo().log(ms, "1", "2", "3", "4", "5")
    backend.assertLastLogged().hasArguments("1", "2", "3", "4", "5")
    logger.atInfo().log(ms, "1", "2", "3", "4", "5", "6")
    backend.assertLastLogged().hasArguments("1", "2", "3", "4", "5", "6")
    logger.atInfo().log(ms, "1", "2", "3", "4", "5", "6", "7")
    backend.assertLastLogged().hasArguments("1", "2", "3", "4", "5", "6", "7")
    logger.atInfo().log(ms, "1", "2", "3", "4", "5", "6", "7", "8")
    backend.assertLastLogged().hasArguments("1", "2", "3", "4", "5", "6", "7", "8")
    logger.atInfo().log(ms, "1", "2", "3", "4", "5", "6", "7", "8", "9")
    backend.assertLastLogged().hasArguments("1", "2", "3", "4", "5", "6", "7", "8", "9")
    logger.atInfo().log(ms, "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    backend.assertLastLogged().hasArguments("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

    /*
    vararg rest needs to be properly merged into the params array first in log(..)
    logger.atInfo().log(ms, "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11")
    backend
        .assertLastLogged()
        .hasArguments("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11")
    logger.atInfo().log(ms, "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    backend
        .assertLastLogged()
        .hasArguments("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
     */
  }

  @Test
  fun testOneUnboxedArgument() {
    val backend = KFakeLoggerBackend()
    val logger = KFluentLogger(backend)
    val ms = "Any message will do..."

    // Verify arguments passed in to the non-boxed fundamental type methods are mapped correctly.
    logger.atInfo().log(ms, BYTE_ARG)
    backend.assertLastLogged().hasArguments(BYTE_ARG)
    logger.atInfo().log(ms, SHORT_ARG)
    backend.assertLastLogged().hasArguments(SHORT_ARG)
    logger.atInfo().log(ms, INT_ARG)
    backend.assertLastLogged().hasArguments(INT_ARG)
    logger.atInfo().log(ms, LONG_ARG)
    backend.assertLastLogged().hasArguments(LONG_ARG)
    logger.atInfo().log(ms, CHAR_ARG)
    backend.assertLastLogged().hasArguments(CHAR_ARG)
  }

  @Test
  fun testTwoUnboxedArguments() {
    val backend = KFakeLoggerBackend()
    val logger = KFluentLogger(backend)
    val ms = "Any message will do..."

    // Verify arguments passed in to the non-boxed fundamental type methods are mapped correctly.
    logger.atInfo().log(ms, BYTE_ARG, BYTE_ARG)
    backend.assertLastLogged().hasArguments(BYTE_ARG, BYTE_ARG)
    logger.atInfo().log(ms, BYTE_ARG, SHORT_ARG)
    backend.assertLastLogged().hasArguments(BYTE_ARG, SHORT_ARG)
    logger.atInfo().log(ms, BYTE_ARG, INT_ARG)
    backend.assertLastLogged().hasArguments(BYTE_ARG, INT_ARG)
    logger.atInfo().log(ms, BYTE_ARG, LONG_ARG)
    backend.assertLastLogged().hasArguments(BYTE_ARG, LONG_ARG)
    logger.atInfo().log(ms, BYTE_ARG, CHAR_ARG)
    backend.assertLastLogged().hasArguments(BYTE_ARG, CHAR_ARG)
    logger.atInfo().log(ms, SHORT_ARG, BYTE_ARG)
    backend.assertLastLogged().hasArguments(SHORT_ARG, BYTE_ARG)
    logger.atInfo().log(ms, SHORT_ARG, SHORT_ARG)
    backend.assertLastLogged().hasArguments(SHORT_ARG, SHORT_ARG)
    logger.atInfo().log(ms, SHORT_ARG, INT_ARG)
    backend.assertLastLogged().hasArguments(SHORT_ARG, INT_ARG)
    logger.atInfo().log(ms, SHORT_ARG, LONG_ARG)
    backend.assertLastLogged().hasArguments(SHORT_ARG, LONG_ARG)
    logger.atInfo().log(ms, SHORT_ARG, CHAR_ARG)
    backend.assertLastLogged().hasArguments(SHORT_ARG, CHAR_ARG)
    logger.atInfo().log(ms, INT_ARG, BYTE_ARG)
    backend.assertLastLogged().hasArguments(INT_ARG, BYTE_ARG)
    logger.atInfo().log(ms, INT_ARG, SHORT_ARG)
    backend.assertLastLogged().hasArguments(INT_ARG, SHORT_ARG)
    logger.atInfo().log(ms, INT_ARG, INT_ARG)
    backend.assertLastLogged().hasArguments(INT_ARG, INT_ARG)
    logger.atInfo().log(ms, INT_ARG, LONG_ARG)
    backend.assertLastLogged().hasArguments(INT_ARG, LONG_ARG)
    logger.atInfo().log(ms, INT_ARG, CHAR_ARG)
    backend.assertLastLogged().hasArguments(INT_ARG, CHAR_ARG)
    logger.atInfo().log(ms, LONG_ARG, BYTE_ARG)
    backend.assertLastLogged().hasArguments(LONG_ARG, BYTE_ARG)
    logger.atInfo().log(ms, LONG_ARG, SHORT_ARG)
    backend.assertLastLogged().hasArguments(LONG_ARG, SHORT_ARG)
    logger.atInfo().log(ms, LONG_ARG, INT_ARG)
    backend.assertLastLogged().hasArguments(LONG_ARG, INT_ARG)
    logger.atInfo().log(ms, LONG_ARG, LONG_ARG)
    backend.assertLastLogged().hasArguments(LONG_ARG, LONG_ARG)
    logger.atInfo().log(ms, LONG_ARG, CHAR_ARG)
    backend.assertLastLogged().hasArguments(LONG_ARG, CHAR_ARG)
    logger.atInfo().log(ms, CHAR_ARG, BYTE_ARG)
    backend.assertLastLogged().hasArguments(CHAR_ARG, BYTE_ARG)
    logger.atInfo().log(ms, CHAR_ARG, SHORT_ARG)
    backend.assertLastLogged().hasArguments(CHAR_ARG, SHORT_ARG)
    logger.atInfo().log(ms, CHAR_ARG, INT_ARG)
    backend.assertLastLogged().hasArguments(CHAR_ARG, INT_ARG)
    logger.atInfo().log(ms, CHAR_ARG, LONG_ARG)
    backend.assertLastLogged().hasArguments(CHAR_ARG, LONG_ARG)
    logger.atInfo().log(ms, CHAR_ARG, CHAR_ARG)
    backend.assertLastLogged().hasArguments(CHAR_ARG, CHAR_ARG)
  }

  @Test
  fun testTwoMixedArguments() {
    val backend = KFakeLoggerBackend()
    val logger = KFluentLogger(backend)
    val ms = "Any message will do..."

    // Verify arguments passed in to the non-boxed fundamental type methods are mapped correctly.
    logger.atInfo().log(ms, OBJECT_ARG, BYTE_ARG)
    backend.assertLastLogged().hasArguments(OBJECT_ARG, BYTE_ARG)
    logger.atInfo().log(ms, OBJECT_ARG, SHORT_ARG)
    backend.assertLastLogged().hasArguments(OBJECT_ARG, SHORT_ARG)
    logger.atInfo().log(ms, OBJECT_ARG, INT_ARG)
    backend.assertLastLogged().hasArguments(OBJECT_ARG, INT_ARG)
    logger.atInfo().log(ms, OBJECT_ARG, LONG_ARG)
    backend.assertLastLogged().hasArguments(OBJECT_ARG, LONG_ARG)
    logger.atInfo().log(ms, OBJECT_ARG, CHAR_ARG)
    backend.assertLastLogged().hasArguments(OBJECT_ARG, CHAR_ARG)
    logger.atInfo().log(ms, BYTE_ARG, OBJECT_ARG)
    backend.assertLastLogged().hasArguments(BYTE_ARG, OBJECT_ARG)
    logger.atInfo().log(ms, SHORT_ARG, OBJECT_ARG)
    backend.assertLastLogged().hasArguments(SHORT_ARG, OBJECT_ARG)
    logger.atInfo().log(ms, INT_ARG, OBJECT_ARG)
    backend.assertLastLogged().hasArguments(INT_ARG, OBJECT_ARG)
    logger.atInfo().log(ms, LONG_ARG, OBJECT_ARG)
    backend.assertLastLogged().hasArguments(LONG_ARG, OBJECT_ARG)
    logger.atInfo().log(ms, CHAR_ARG, OBJECT_ARG)
    backend.assertLastLogged().hasArguments(CHAR_ARG, OBJECT_ARG)
  }

  @Test
  fun testLazyArgs() {
    val backend = KFakeLoggerBackend()
    val logger = KFluentLogger(backend)
    logger.atInfo().log("Hello %s", KLazyArgs.lazy { "World" })
    logger
        .atFine()
        .log("Hello %s", KLazyArgs.lazy { throw AssertionError("Should not be evaluated") })

    // By the time the backend processes a log statement, lazy arguments have been evaluated.
    backend.assertLastLogged().hasMessage("Hello %s")
    backend.assertLastLogged().hasArguments("World")
  }
}
