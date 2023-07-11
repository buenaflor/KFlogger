/*
 * Copyright (C) 2020 The Flogger Authors.
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
package com.buenaflor.kflogger

import org.checkerframework.checker.nullness.compatqual.NullableDecl

/**
 * Provides a scope to a log statement via the [LogContext.per] method.
 *
 * This interface exists to avoid needing to pass specific instances of [LoggingScope] around in
 * user code. The scope provider can lookup the correct scope instance for the current thread, and
 * different providers can provide different types of scope (e.g. you can have a provider for
 * "request" scopes and a provider for "sub-task" scopes)
 */
public actual interface LoggingScopeProvider {
  @get:NullableDecl public actual val currentScope: LoggingScope?
}
