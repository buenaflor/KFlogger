package com.buenaflor.kflogger.backend

import com.buenaflor.kflogger.parser.KMessageParser

/**
 * A context object for templates that allows caches to validate existing templates or create new
 * ones. If two template contexts are equal (via [.equals]) then the templates they produce are
 * interchangeable.
 *
 * Template contexts are created by the frontend and passed through to backend implementations via
 * the [KLogData] interface.
 */
public actual class KTemplateContext
actual constructor(private val parser: KMessageParser, public val message: String) {
    actual override fun equals(other: Any?): Boolean {
        if (other is KTemplateContext) {
            return parser == other.parser && message == other.message
        }
        return false
    }

    actual override fun hashCode(): Int {
        // We don't expect people to be using the context as a cache key, but it should work.
        return parser.hashCode() xor message.hashCode()
    }

    /** Returns the message parser for the log statement. */
    public actual fun getParser(): KMessageParser {
        return parser
    }

    /** Returns the message for the log statement. */
    public actual fun getMessage(): String {
        return message
    }
}
