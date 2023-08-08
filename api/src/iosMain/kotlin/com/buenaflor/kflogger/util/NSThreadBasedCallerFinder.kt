package com.buenaflor.kflogger.util

import com.buenaflor.kflogger.KAbstractLogger
import com.buenaflor.kflogger.KLogSite
import com.buenaflor.kflogger.Klass
import com.buenaflor.kflogger.backend.KPlatformLogCallerFinder
import platform.Foundation.NSThread

public class NSThreadBasedCallerFinder private constructor() : KPlatformLogCallerFinder() {
  private fun findCallerNameOf(target: Klass<*>): String {
    val name = target.kClass.qualifiedName
    checkNotNull(name) { "Logger class has no name" }
    val callstackSymbols = NSThread.callStackSymbols
    check(callstackSymbols.size > 1) { "Callstack is too short" }
    val firstIndex = findIndexOfStringContainingSubstring(callstackSymbols as List<String>, target.kClass.qualifiedName + ".")
    val stackframeString = callstackSymbols[firstIndex + 1]
    val stackFrameSignature = stackframeString.substringAfter("kfun:").substringBefore("#")
    val lastIndexOfDot = stackFrameSignature.lastIndexOf(".")
    check(lastIndexOfDot != -1) { "Stackframe signature does not contain a dot" }
    if (stackFrameSignature[lastIndexOfDot + 1].isLowerCase()) {
      // If the last character before the dot is lowercase, we assume it's a function name
      return stackFrameSignature.substring(0, lastIndexOfDot)
    }
    return stackFrameSignature
  }

  private fun findIndexOfStringContainingSubstring(stackTrace: List<String>, substring: String): Int {
    for ((index, line) in stackTrace.withIndex()) {
      if (line.contains(substring)) {
        return index
      }
    }
    return -1
  }


  override fun findLoggingClass(loggerClass: Klass<out KAbstractLogger<*>>): String {
    return findCallerNameOf(loggerClass)
  }

  override fun findLogSite(loggerApi: Klass<*>, stackFramesToSkip: Int): KLogSite {
    TODO("Not yet implemented")
  }

  public companion object {
    public val instance: NSThreadBasedCallerFinder = NSThreadBasedCallerFinder()
  }
}
