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

import com.buenaflor.kflogger.util.Checks
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * An opaque scope marker which can be attached to log sites to provide "per scope" behaviour for
 * stateful logging operations (e.g. rate limiting).
 *
 *
 * Scopes are provided via the [Provider] interface and found by looking for
 * the current [ScopedLoggingContexts][com.google.common.flogger.context.ScopedLoggingContext].
 *
 *
 * Stateful fluent logging APIs which need to look up per log site information (e.g. rate limit
 * state) should do so via a [LogSiteMap] using the [LogSiteKey] passed into the [ ][LogContext.postProcess] method. If scopes are present in the log site [ ] then the log site key provided to the `postProcess()` method will already be
 * specialized to take account of any scopes present.
 *
 *
 * Note that scopes have no effect when applied to stateless log statements (e.g. log statements
 * without rate limiting) since the log site key for that log statement will not be used in any
 * maps.
 */
public actual abstract class LoggingScope
/**
 * Creates a basic scope with the specified label. Custom subclasses of `LoggingScope` must
 * manage their own lifecycles to avoid leaking memory and polluting [LogSiteMap]s with
 * unused keys.
 */ protected actual constructor(private val label: String) {
    /**
     * Returns a specialization of the given key which accounts for this scope instance. Two
     * specialized keys should compare as [Object.equals] if and only if they are
     * specializations from the same log site, with the same sequence of scopes applied.
     *
     *
     * The returned instance:
     *
     *
     *  * Must be an immutable "value type".
     *  * Must not compare as [Object.equals] to the given key.
     *  * Should have a different [Object.hashCode] to the given key.
     *  * Should be efficient and lightweight.
     *
     *
     * As such it is recommended that the [SpecializedLogSiteKey.of] method
     * is used in implementations, passing in a suitable qualifier (which need not be the scope
     * itself, but must be unique per scope).
     */
    protected actual abstract fun specialize(key: LogSiteKey?): LogSiteKey?

    /**
     * Registers "hooks" which should be called when this scope is "closed". The hooks are intended to
     * remove the keys associated with this scope from any data structures they may be held in, to
     * avoid leaking allocations.
     *
     *
     * Note that a key may be specialized with several scopes and the first scope to be closed will
     * remove it from any associated data structures (conceptually the scope that a log site is called
     * from is the intersection of all the currently active scopes which apply to it).
     */
    protected abstract fun onClose(removalHook: Runnable?)
    actual override fun toString(): String {
        return label
    }

    // VisibleForTesting
    internal class WeakScope(label: String) : LoggingScope(label) {
        // Do NOT reference the Scope directly from a specialized key, use the "key part"
        // to avoid the key from keeping the Scope instance alive. When the scope becomes
        // unreachable, the key part weak reference is enqueued which triggers tidyup at
        // the next call to specializeForScopesIn() where scopes are used.
        //
        // This must be unique per scope since it acts as a qualifier within specialized
        // log site keys. Using a different weak reference per specialized key would not
        // work (which is part of the reason we also need the "on close" queue as well as
        // the reference queue).
        private val keyPart: KeyPart

        init {
            keyPart = KeyPart(this)
        }

        override fun specialize(key: LogSiteKey?): LogSiteKey {
            return SpecializedLogSiteKey.of(key, keyPart)
        }

        protected override fun onClose(remove: Runnable?) {
            // Clear the reference queue about as often as we would add a new key to a map.
            // This  should still mean that the queue is almost always empty when we check
            // it (since we expect more than one specialized log site key per scope) and it
            // avoids spamming the queue clearance loop for every log statement and avoids
            // class loading the reference queue until we know scopes have been used.
            KeyPart.removeUnusedKeys()
            keyPart.onCloseHooks.offer(remove)
        }

        fun closeForTesting() {
            keyPart.close()
        }

        // Class is only loaded once we've seen scopes in action (Android doesn't like
        // eager class loading and many Android apps won't use scopes).
        // This forms part of each log site key, some must have singleton semantics.
        private class KeyPart(scope: LoggingScope?) :
            WeakReference<LoggingScope>(scope, queue) { val onCloseHooks: Queue<Runnable> = ConcurrentLinkedQueue()
            fun close() {
                // This executes once for each map entry created in the enclosing scope. It is
                // very dependent on logging usage in the scope and theoretically unbounded.
                var r = onCloseHooks.poll()
                while (r != null) {
                    r.run()
                    r = onCloseHooks.poll()
                }
            }

            companion object {
                private val queue = ReferenceQueue<LoggingScope>()

                // If this were ever too "bursty" due to removal of many keys for the same scope,
                // we could modify this code to process only a maximum number of removals each
                // time and keep a single "in progress" KeyPart around until next time.
                fun removeUnusedKeys() {
                    // There are always more specialized keys than entries in the reference queue,
                    // so the queue should be empty most of the time we get here.
                    var p = queue.poll() as? KeyPart
                    while (p != null) {
                        p.close()
                        p = queue.poll() as? KeyPart
                    }
                }
            }
        }
    }

    public actual companion object {
        /**
         * Creates a scope which automatically removes any associated keys from [LogSiteMap]s when
         * it's garbage collected. The given label is used only for debugging purposes and may appear in
         * log statements, it should not contain any user data or other runtime information.
         */
        // TODO: Strongly consider making the label a compile time constant.
        @JvmStatic
        public actual fun create(label: String): LoggingScope {
            return WeakScope(Checks.checkNotNull(label, "label"))
        }
    }
}
