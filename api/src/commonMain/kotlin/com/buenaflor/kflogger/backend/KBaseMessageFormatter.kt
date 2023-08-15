package com.buenaflor.kflogger.backend

import com.buenaflor.kflogger.parameter.KDateTimeFormat
import com.buenaflor.kflogger.parameter.KParameter
import com.buenaflor.kflogger.parameter.KParameterVisitor
import com.buenaflor.kflogger.parser.KMessageBuilder

/**
 * The default formatter for log messages and arguments.
 *
 * This formatter can be overridden to modify the behaviour of the [ParameterVisitor] methods, but
 * this is not expected to be common. Most logger backends will only ever need to use
 * [.appendFormattedMessage].
 */
public expect open class KBaseMessageFormatter
protected constructor(context: KTemplateContext, args: Array<Any?>?, out: StringBuilder) :
    KMessageBuilder<StringBuilder>, KParameterVisitor {

  public override fun addParameterImpl(termStart: Int, termEnd: Int, param: KParameter)

  public override fun buildImpl(): StringBuilder

  override fun visit(value: Any?, format: KFormatChar?, options: KFormatOptions?)

  override fun visitDateTime(value: Any?, format: KDateTimeFormat?, options: KFormatOptions?)

  override fun visitPreformatted(value: Any?, formatted: String?)

  override fun visitMissing()

  override fun visitNull()

  public companion object {
    /**
     * Appends the formatted log message of the given log data to the given buffer.
     *
     * Note that the [LogData] need not have a template context or arguments, it might just have a
     * literal argument, which will be appended without additional formatting.
     *
     * @param data the log data with the message to be appended.
     * @param out a buffer to append to.
     * @return the given buffer (for method chaining).
     */
    public fun appendFormattedMessage(data: KLogData, out: StringBuilder): StringBuilder
  }
}
