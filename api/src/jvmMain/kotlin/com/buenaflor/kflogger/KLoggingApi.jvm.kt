package com.buenaflor.kflogger

import com.buenaflor.kflogger.backend.KMetadata

public actual typealias KLoggingApi<API> = LoggingApi<API>

@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
public actual typealias KLoggingApiNoOp<API> = LoggingApi.NoOp<API>

// ---- LogData API ----

public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<LOGGER, API>.level: KLevel? get() = level

@Deprecated("")
public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<LOGGER, API>.timestampMicros: Long get() = timestampMicros

public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<LOGGER, API>.timestampNanos: Long get() = timestampNanos

public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<LOGGER, API>.loggerName: String? get() = loggerName

public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<LOGGER, API>.logSite: KLogSite? get() = logSite

// TODO KFlogger: public expect val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<LOGGER, API>.templateContext: TemplateContext

public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<LOGGER, API>.arguments: Array<Any?>? get() = arguments

public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<LOGGER, API>.literalArguments: Any? get() = literalArgument

public actual val <LOGGER : KAbstractLogger<API>, API : KLoggingApi<API>> KLogContext<LOGGER, API>.metadata: KMetadata? get() = metadata