package com.buenaflor.kflogger.util

import com.buenaflor.kflogger.KAbstractLogger
import com.buenaflor.kflogger.KLogSite
import com.buenaflor.kflogger.Klass
import com.buenaflor.kflogger.backend.KLogCallerFinder
import platform.Foundation.NSThread

public class NSThreadBasedCallerFinder private constructor() : KLogCallerFinder() {
    private fun findCallerNameOf(target: Klass<*>): String {
        val name = target.kClass.qualifiedName
        checkNotNull(name) { "Logger class has no name" }
        val callstackSymbols = NSThread.callStackSymbols
        check(callstackSymbols.size > 1) { "Callstack is too short" }
        val stackframeString = callstackSymbols[2] as String
        check(stackframeString.contains(name)) { "Stackframe does not contain $name" }
        return stackframeString.substringAfter("kfun:").substringBefore("#")
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