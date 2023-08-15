package com.buenaflor.kflogger.backend

import com.buenaflor.kflogger.parameter.KDateTimeFormat
import com.buenaflor.kflogger.parameter.KParameter
import com.buenaflor.kflogger.parameter.KParameterVisitor
import com.buenaflor.kflogger.parser.KMessageBuilder

public actual open class KBaseMessageFormatter protected actual constructor(
    context: KTemplateContext,
    private val args: Array<Any?>?,
    private val out: StringBuilder
) : KMessageBuilder<StringBuilder>(context), KParameterVisitor {
    public actual override fun addParameterImpl(
        termStart: Int,
        termEnd: Int,
        param: KParameter
    ) {
        TODO("Not yet implemented")
    }

    public actual override fun buildImpl(): StringBuilder {
        TODO("Not yet implemented")
    }

    actual override fun visit(value: Any?, format: KFormatChar?, options: KFormatOptions?) {
        TODO("Not yet implemented")
    }

    actual override fun visitDateTime(value: Any?, format: KDateTimeFormat?, options: KFormatOptions?) {
        TODO("Not yet implemented")
    }

    actual override fun visitPreformatted(value: Any?, formatted: String?) {
        // For unstructured logging we just use the pre-formatted string.
        TODO("Not yet implemented")
    }

    actual override fun visitMissing() {
        TODO("Not yet implemented")
    }

    actual override fun visitNull() {
        TODO("Not yet implemented")
    }

    public actual companion object {
        // Literal string to be inlined whenever a placeholder references a non-existent argument.
        private const val MISSING_ARGUMENT_MESSAGE = "[ERROR: MISSING LOG ARGUMENT]"

        // Literal string to be appended wherever additional unused arguments are provided.
        private const val EXTRA_ARGUMENT_MESSAGE = " [ERROR: UNUSED LOG ARGUMENTS]"

        /**
         * Appends the formatted log message of the given log data to the given buffer.
         *
         *
         * Note that the [LogData] need not have a template context or arguments, it might just
         * have a literal argument, which will be appended without additional formatting.
         *
         * @param data the log data with the message to be appended.
         * @param out a buffer to append to.
         * @return the given buffer (for method chaining).
         */
        public actual fun appendFormattedMessage(
            data: KLogData,
            out: StringBuilder
        ): StringBuilder {
            TODO("Not yet implemented")
        }

        private fun appendInvalid(out: StringBuilder, value: Any, formatString: String) {
            TODO("Not yet implemented")
        }
    }

}