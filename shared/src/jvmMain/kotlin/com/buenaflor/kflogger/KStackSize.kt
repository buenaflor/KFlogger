package com.buenaflor.kflogger

actual typealias KStackSize = StackSize

/**
 * Returns the maximum stack depth to create when adding contextual stack information to a log
 * statement.
 *
 *
 * Note that the precise number of stack elements emitted for the enum values might change over
 * time, but it can be assumed that `NONE < SMALL <= MEDIUM <= LARGE <= FULL`.
 */
actual val KStackSize.maxDepth: Int get() = maxDepth



