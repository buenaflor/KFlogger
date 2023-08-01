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
package com.buenaflor.kflogger.parameter

import com.buenaflor.kflogger.backend.KFormatOptions

/**
 * An abstract representation of a parameter for a message template.
 *
 * Note that this is implemented as a class (rather than via an interface) because it is very
 * helpful to have explicit checks for the index values and count to ensure we can calculate
 * reliable low bounds for the number of arguments a template can accept.
 *
 * Note that all subclasses of Parameter must be immutable and thread safe.
 */
public expect abstract class KParameter protected constructor(options: KFormatOptions, index: Int) {
  /** Returns the formatting options. */
  protected val formatOptions: KFormatOptions

  public fun accept(visitor: KParameterVisitor, args: Array<Any?>)

  protected abstract fun accept(visitor: KParameterVisitor, value: Any)

  /** Returns the printf format string specified for this parameter (eg, "%d" or "%tc"). */
  public abstract fun getFormat(): String
}

/** Returns the index of the argument to be processed by this parameter. */
public expect fun KParameter.index(): Int
