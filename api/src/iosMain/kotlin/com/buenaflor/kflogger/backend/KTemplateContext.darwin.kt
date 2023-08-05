package com.buenaflor.kflogger.backend

import com.buenaflor.kflogger.parser.KMessageParser

/**
 * A context object for templates that allows caches to validate existing templates or create new
 * ones. If two template contexts are equal (via [.equals]) then the templates they produce are
 * interchangeable.
 *
 * Template contexts are created by the frontend and passed through to backend implementations via
 * the [LogData] interface.
 */
public actual class KTemplateContext
actual constructor(parser: KMessageParser?, public val message: String) {
  actual override fun equals(other: Any?): Boolean {
    TODO()
  }

  actual override fun hashCode(): Int {
    TODO()
  }
}

/** Returns the message parser for the log statement. */
public actual val KTemplateContext.parser: KMessageParser
  get() = TODO()

/** Returns the message for the log statement. */
public actual val KTemplateContext.message: String
  get() = message
