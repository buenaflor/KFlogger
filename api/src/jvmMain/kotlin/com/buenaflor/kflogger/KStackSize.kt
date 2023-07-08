package com.buenaflor.kflogger

actual typealias KStackSize = StackSize

actual val KStackSize.maxDepth: Int
  get() = maxDepth
