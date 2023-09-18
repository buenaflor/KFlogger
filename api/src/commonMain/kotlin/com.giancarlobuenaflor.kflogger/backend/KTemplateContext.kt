/*
 * Copyright (C) 2014 The Flogger Authors.
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
package com.giancarlobuenaflor.kflogger.backend

import com.giancarlobuenaflor.kflogger.parser.KMessageParser

/**
 * A context object for templates that allows caches to validate existing templates or create new
 * ones. If two template contexts are equal (via [.equals]) then the templates they produce are
 * interchangeable.
 *
 * Template contexts are created by the frontend and passed through to backend implementations via
 * the [KLogData] interface.
 */
public expect class KTemplateContext(parser: KMessageParser, message: String) {
  override fun equals(other: Any?): Boolean

  override fun hashCode(): Int

  /** Returns the message parser for the log statement. */
  public fun getParser(): KMessageParser

  /** Returns the message for the log statement. */
  public fun getMessage(): String
}
