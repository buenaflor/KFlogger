package com.buenaflor.kflogger.backend

import com.buenaflor.kflogger.parser.KMessageParser

public actual typealias KTemplateContext = TemplateContext

public actual val KTemplateContext.parser: KMessageParser get() = parser

public actual val KTemplateContext.message: String get() = message
