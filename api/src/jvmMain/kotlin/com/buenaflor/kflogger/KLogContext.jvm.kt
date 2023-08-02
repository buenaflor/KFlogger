package com.buenaflor.kflogger

import com.buenaflor.kflogger.backend.KMetadata
import com.buenaflor.kflogger.backend.KTemplateContext

// Suppressed error: protected visibility not matching between Java and Kotlin
@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
public actual typealias KLogContext<LOGGER, API> = LogContext<LOGGER, API>

// Suppressed error: companion object
@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
public actual typealias KLogContextKey = LogContext.Key

public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.level: KLevel?
  get() = level

@Deprecated("")
public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.timestampMicros: Long
  get() = timestampMicros

public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.timestampNanos: Long
  get() = timestampNanos

public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.loggerName: String?
  get() = loggerName

public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.logSite: KLogSite?
  get() = logSite

public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.templateContext: KTemplateContext
  get() = templateContext

public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.arguments: Array<Any?>?
  get() = arguments

public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.literalArguments: Any?
  get() = literalArgument

public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<
    LOGGER, API>.metadata: KMetadata?
  get() = metadata
