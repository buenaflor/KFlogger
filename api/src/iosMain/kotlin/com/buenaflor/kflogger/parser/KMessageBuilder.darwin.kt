/*
 * Copyright (C) 2012 The Flogger Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.buenaflor.kflogger.parser

import com.buenaflor.kflogger.backend.KTemplateContext
import com.buenaflor.kflogger.parameter.KParameter

/**
 * A builder which is used during message parsing to create a message object which encapsulates
 * all the formatting requirements of a log message. One message builder is created for each log
 * message that's parsed.
 *
 * @param <T> The message type being built.
</T> */
public actual abstract class KMessageBuilder<T> actual constructor(context: KTemplateContext) {
    /**
     * Called by parser implementations to signify that the parsing of the next parameter is complete.
     * This method will call [.addParameterImpl] with exactly the same
     * arguments, but may also do additional work before or after that call.
     *
     * @param termStart the index of the first character in the log message string that was parsed to
     * form the given parameter.
     * @param termEnd the index after the last character in the log message string that was parsed to
     * form the given parameter.
     * @param param a parameter representing the format specified by the substring of the log message
     * in the range `[termStart, termEnd)`.
     */
    public actual fun addParameter(termStart: Int, termEnd: Int, param: KParameter) {
    }

    /**
     * Adds the specified parameter to the format instance currently being built. This method is to
     * signify that the parsing of the next parameter is complete.
     *
     *
     * Note that each successive call to this method during parsing will specify a disjoint ranges of
     * characters from the log message and that each range will be higher that the previously
     * specified one.
     *
     * @param termStart the index of the first character in the log message string that was parsed to
     * form the given parameter.
     * @param termEnd the index after the last character in the log message string that was parsed to
     * form the given parameter.
     * @param param a parameter representing the format specified by the substring of the log message
     * in the range `[termStart, termEnd)`.
     */
    protected actual abstract fun addParameterImpl(termStart: Int, termEnd: Int, param: KParameter)

    /** Returns the implementation specific result of parsing the current log message.  */
    protected actual abstract fun buildImpl(): T

    /**
     * Builds a log message using the current message context.
     *
     * @return the implementation specific result of parsing the current log message.
     */
    public actual fun build(): T {
        TODO("Not yet implemented")
    }
}

/** Returns the parser used to process the log format message in this builder.  */
public actual val <T> KMessageBuilder<T>.parser: KMessageParser get() = TODO()

/** Returns the log format message to be parsed by this builder.  */
public actual val <T> KMessageBuilder<T>.message: String get() = TODO()

/**
 * Returns the expected number of arguments to be formatted by this message. This is only valid
 * once parsing has completed successfully.
 */
public actual val <T> KMessageBuilder<T>.expectedArgumentCount: Int get() = TODO()
