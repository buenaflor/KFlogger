package com.buenaflor.kflogger.parser

// Suppress errors: protected Java visibility - does not match Kotlin protected visibility
@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
public actual typealias KMessageBuilder<T> = MessageBuilder<T>

public actual val <T> KMessageBuilder<T>.parser: KMessageParser
  get() = parser

public actual val <T> KMessageBuilder<T>.message: String
  get() = message

public actual val <T> KMessageBuilder<T>.expectedArgumentCount: Int
  get() = expectedArgumentCount
