/*
 * Copyright (C) 2012 The Flogger Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.buenaflor.kflogger

import com.buenaflor.kflogger.util.Checks
import org.checkerframework.checker.nullness.compatqual.NullableDecl

/**
 * A value type which representing the location of a single log statement. This class is similar to
 * the `StackTraceElement` class but differs in one important respect.
 *
 * A LogSite can be associated with a globally unique ID, which can identify a log statement more
 * uniquely than a line number (it is possible to have multiple log statements appear to be on a
 * single line, especially for obfuscated classes).
 *
 * Log sites are intended to be injected into code automatically, typically via some form of
 * bytecode rewriting. Each injection mechanism can have its own implementation of `LogSite` adapted
 * to its needs.
 *
 * As a fallback, for cases where no injection mechanism is configured, a log site based upon stack
 * trace analysis is used. However due to limitations in the information available from
 * `StackTraceElement`, this log site will not be unique if multiple log statements are on the the
 * same, or if line number information was stripped from the class file.
 */
public actual abstract class LogSite : LogSiteKey {
  /** Returns the name of the class containing the log statement. */
  public actual abstract val className: String?

  /** Returns the name of the method containing the log statement. */
  public actual abstract val methodName: String?

  /**
   * Returns a valid line number for the log statement in the range 1 - 65535, or [.UNKNOWN_LINE] if
   * not known.
   *
   * There is a limit of 16 bits for line numbers in a class. See
   * [here](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.12) for more
   * details.
   */
  public actual abstract val lineNumber: Int

  @get:NullableDecl public actual abstract val fileName: String?

  // Provide a common toString() implementation for only the public attributes.
  actual override fun toString(): String {
    val out =
        StringBuilder()
            .append("LogSite{ class=")
            .append(this.className)
            .append(", method=")
            .append(this.methodName)
            .append(", line=")
            .append(this.lineNumber)
    if (this.fileName != null) {
      out.append(", file=").append(this.fileName)
    }
    return out.append(" }").toString()
  }

  private class InjectedLogSite(
      internalClassName: String,
      methodName: String,
      private val encodedLineNumber: Int,
      @field:NullableDecl @param:NullableDecl private val sourceFileName: String,
  ) : LogSite() {
    /** Internal (slash-separated) fully qualified class name (eg, "com/example/Foo$Bar"). */
    private var internalClassName: String

    override val className: String
      get() {
        // We have to do the conversion from internal to public class name somewhere, and doing it
        // earlier could cost work in cases where the log statement is dropped. We could cache the
        // result somewhere, but in the default Fluent Logger backend, this method is actually only
        // called once anyway when constructing the LogRecord instance.
        return internalClassName.replace('/', '.')
      }

    /** Bare method name (no signature information). */
    override val methodName: String = Checks.checkNotNull(methodName, "method name")

    override val lineNumber: Int
      get() = encodedLineNumber and 0xFFFF

    @NullableDecl
    override val fileName: String?
      get() = sourceFileName

    private var hashcode = 0

    init {
      this.internalClassName = Checks.checkNotNull(internalClassName, "class name")
    }

    override fun equals(obj: Any?): Boolean {
      if (obj is InjectedLogSite) {
        val other = obj
        // Probably not worth optimizing for "this == obj" because all strings should be interned.
        return internalClassName == other.internalClassName &&
            methodName == other.methodName &&
            encodedLineNumber == other.encodedLineNumber
      }
      return false
    }

    override fun hashCode(): Int {
      if (hashcode == 0) {
        // TODO(dbeaumont): Revisit the algorithm when looking at b/22753674.
        // If the log statement uses metadata, the log site will be used as a key to look up the
        // current value. In most cases the hashcode is never needed, but in others it may be used
        // multiple times in different data structures.
        var temp = 157
        temp = 31 * temp + internalClassName.hashCode()
        temp = 31 * temp + methodName.hashCode()
        temp = 31 * temp + encodedLineNumber
        hashcode = temp
      }
      return hashcode
    }
  }

  public actual companion object {
    /** A value used for line numbers when the true information is not available. */
    public actual const val UNKNOWN_LINE: Int = 0

    /**
     * An singleton LogSite instance used to indicate that valid log site information cannot be
     * determined. This can be used to indicate that log site information is not available by
     * injecting it via [LoggingApi.withInjectedLogSite] which will suppress any further log site
     * analysis for that log statement. This is also returned if stack trace analysis fails for any
     * reason.
     *
     * If a log statement does end up with invalid log site information, then any fluent logging
     * methods which rely on being able to look up site specific metadata will be disabled and
     * essentially become "no ops".
     */
    @JvmField
    public actual val INVALID: LogSite =
        object : LogSite() {
          override val className: String?
            get() = "<unknown class>"

          override val methodName: String?
            get() = "<unknown method>"

          override val lineNumber: Int
            get() = UNKNOWN_LINE

          override val fileName: String?
            get() = null // No need to implement equals() or hashCode() for a singleton instance.
        }

    /**
     * Creates a log site injected from constants held a class' constant pool.
     *
     * Used for compile-time log site injection, and by the agent.
     *
     * @param internalClassName Slash separated class name obtained from the class constant pool.
     * @param methodName Method name obtained from the class constant pool.
     * @param encodedLineNumber line number and per-line log statement index encoded as a single
     *   32-bit value. The low 16-bits is the line number (0 to 0xFFFF inclusive) and the high 16
     *   bits is a log statement index to distinguish multiple statements on the same line (this
     *   becomes important if line numbers are stripped from the class file and everything appears
     *   to be on the same line).
     * @param sourceFileName Optional base name of the source file (this value is strictly for
     *   debugging and does not contribute to either equals() or hashCode() behavior).
     */
    @Deprecated(
        """this method is only be used for log-site injection and should not be called
    directly.""")
    @JvmStatic
    public actual fun injectedLogSite(
        internalClassName: String,
        methodName: String,
        encodedLineNumber: Int,
        @NullableDecl sourceFileName: String
    ): LogSite {
      return InjectedLogSite(internalClassName, methodName, encodedLineNumber, sourceFileName)
    }
  }
}
