package com.buenaflor.kflogger

// Suppressed error: protected visibility not matching between Java and Kotlin
@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
public actual typealias KLogContext<LOGGER, API> = LogContext<LOGGER, API>

// Suppressed error: companion object
@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
public actual typealias KLogContextKey = LogContext.Key
