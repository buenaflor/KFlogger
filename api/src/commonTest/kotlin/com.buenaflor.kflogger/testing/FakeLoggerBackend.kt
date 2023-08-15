package com.buenaflor.kflogger.testing

import androidx.kruth.FailureMetadata
import androidx.kruth.Subject
import androidx.kruth.Subject.Factory
import androidx.kruth.assertAbout
import com.buenaflor.kflogger.KLevel
import com.buenaflor.kflogger.backend.KLogData
import com.buenaflor.kflogger.backend.KLoggerBackend
import com.buenaflor.kflogger.intValue

/**
 * A logger backend which captures all `LogData` instances logged to it. This class is mutable and
 * not thread safe.
 */
class KFakeLoggerBackend(private val name: String = "com.example.MyClass") : KLoggerBackend() {
  private var minLevel = KLevel.INFO
  private val logged: MutableList<KLogData> = ArrayList()

  /** Sets the current level of this backend. */
  fun setLevel(level: KLevel) {
    minLevel = level
  }

  val loggedCount: Int
    /** Returns the number of [LogData] entries captured by this backend. */
    get() = logged.size

  /** Returns the `Nth` [LogData] entry captured by this backend. */
  fun getLogged(n: Int): KLogData {
    return logged[n]
  }

  /** Asserts about the `Nth` logged entry. */
  fun assertLogged(n: Int): KLogDataSubject {
    return KLogDataSubject.assertThat(logged[n])
  }

  /** Asserts about the most recent logged entry. */
  fun assertLastLogged(): KLogDataSubject {
    return assertLogged(logged.size - 1)
  }

  override fun getLoggerName(): String {
    return name
  }

  override fun isLoggable(loggedLevel: KLevel): Boolean {
    return loggedLevel.intValue() >= minLevel.intValue()
  }

  override fun log(data: KLogData) {
    logged.add(data)
  }

  override fun handleError(error: RuntimeException, badData: KLogData) {
    throw error
  }
}

class KLogDataSubject private constructor(failureMetadata: FailureMetadata, actual: KLogData) :
    Subject<KLogData>(actual = actual, metadata = failureMetadata) {
  /**
   * Asserts that this log entry's message matches the given value. If the log statement for the
   * entry has only a single argument (no formatting), you can write
   * `assertLogData(e).hasMessage(value);`.
   */
  fun hasMessage(messageOrLiteral: Any?) {
    if (actual?.getTemplateContext() == null) {
      // Expect literal argument (possibly null).
      androidx.kruth
          .assertThat(listOf(actual?.getLiteralArgument()))
          .containsExactly(messageOrLiteral)
    } else {
      androidx.kruth
          .assertThat(actual?.getTemplateContext()?.getMessage())
          .isEqualTo(messageOrLiteral)
    }
  }

  /**
   * Asserts that this log entry's arguments match the given values. If the log statement for the
   * entry only a single argument (no formatting), you can write `assertLogData(e).hasArguments();`.
   */
  fun hasArguments(vararg args: Any?) {
    var actualArgs: List<Any?>? = listOf()
    if (actual?.getTemplateContext() != null) {
      actualArgs = actual?.getArguments()?.flatMap { listOf(it) }
    }
    androidx.kruth.assertThat(actualArgs?.toList()).containsExactly(*args).inOrder()
  }

  companion object {
    private val LOG_DATA_SUBJECT_FACTORY: Factory<KLogData, KLogDataSubject> =
        Factory { failureMetadata: FailureMetadata, subject: KLogData ->
          KLogDataSubject(failureMetadata, subject)
        }

    fun logData(): Factory<KLogData, KLogDataSubject> {
      return LOG_DATA_SUBJECT_FACTORY
    }

    fun assertThat(logData: KLogData): KLogDataSubject {
      return assertAbout(logData()).that(logData)
    }
  }
}
