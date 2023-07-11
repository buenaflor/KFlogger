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
package com.buenaflor.kflogger

import com.buenaflor.kflogger.testing.FakeLoggerBackend
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.logging.Level

/**
 * Fluent loggers are typically very simple classes whose only real responsibility is as a factory
 * for a specific API implementation. As such it needs very few tests itself.
 *
 * See LogContextTest.java for the vast majority of tests related to base logging behaviour.
 */
@RunWith(JUnit4::class)
class FluentLoggerTestKt {
    @Test
    fun testCreate() {
        val logger = FluentLogger.forEnclosingClass()
        Truth.assertThat(logger.name).isEqualTo(FluentLoggerTestKt::class.java.name)

        // Note that this one-to-one binding of loggers and backends is not strictly necessary and in
        // future it's plausible that a configured backend factory might return backends shared with
        // many loggers. In that situation, the logger name must still be the enclosing class name
        // (held separately by the logger itself) while the backend name could differ.
        Truth.assertThat(logger.backend.loggerName).isEqualTo(FluentLoggerTestKt::class.java.name)
    }

    @Test
    fun testNoOp() {
        val backend = FakeLoggerBackend()
        val logger = FluentLogger(backend)
        backend.setLevel(Level.INFO)

        // Down to and including the configured log level are not the no-op instance.
        Truth.assertThat(logger.atSevere()).isNotSameInstanceAs(FluentLogger.NO_OP)
        Truth.assertThat(logger.atSevere()).isInstanceOf(FluentLogger.Context::class.java)
        Truth.assertThat(logger.atWarning()).isNotSameInstanceAs(FluentLogger.NO_OP)
        Truth.assertThat(logger.atWarning()).isInstanceOf(FluentLogger.Context::class.java)
        Truth.assertThat(logger.atInfo()).isNotSameInstanceAs(FluentLogger.NO_OP)
        Truth.assertThat(logger.atInfo()).isInstanceOf(FluentLogger.Context::class.java)

        // Below the configured log level you only get the singleton no-op instance.
        Truth.assertThat(logger.atFine()).isSameInstanceAs(FluentLogger.NO_OP)
        Truth.assertThat(logger.atFiner()).isSameInstanceAs(FluentLogger.NO_OP)
        Truth.assertThat(logger.atFinest()).isSameInstanceAs(FluentLogger.NO_OP)

        // Just verify that logs below the current log level are discarded.
        logger.atFine().log("DISCARDED")
        logger.atFiner().log("DISCARDED")
        logger.atFinest().log("DISCARDED")
        Truth.assertThat(backend.loggedCount).isEqualTo(0)

        // But those at or above are passed to the backend.
        logger.atInfo().log("LOGGED")
        Truth.assertThat(backend.loggedCount).isEqualTo(1)
        backend.setLevel(Level.OFF)
        Truth.assertThat(logger.atSevere()).isSameInstanceAs(FluentLogger.NO_OP)
        backend.setLevel(Level.ALL)
        Truth.assertThat(logger.atFinest()).isNotSameInstanceAs(FluentLogger.NO_OP)
    }
}
