package com.giancarlobuenaflor.kflogger.util

import com.giancarlobuenaflor.kflogger.backend.system.formatArgs
import kotlin.test.Test

actual typealias IgnoreIos = kotlin.test.Ignore

class Test {
    @Test
    fun test() {
        print("hallo %s %d %i".formatArgs(arrayOf("das", 0.0, 5)))
    }
}