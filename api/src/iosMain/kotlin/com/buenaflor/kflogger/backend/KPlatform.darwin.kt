package com.buenaflor.kflogger.backend

import com.buenaflor.kflogger.KAbstractLogger
import com.buenaflor.kflogger.KLevel
import com.buenaflor.kflogger.KLogSite
import com.buenaflor.kflogger.Klass
import com.buenaflor.kflogger.context.KTags

/**
 * Platform abstraction layer required to allow fluent logger implementations to work on differing
 * Java platforms (such as Android or GWT). The `Platform` class is responsible for providing
 * any platform specific APIs, including the mechanism by which logging backends are created.
 *
 *
 * To enable an additional logging platform implementation, the class name should be added to
 * the list of available platforms before the default platform (which must always be at the end).
 * Platform implementation classes must subclass `Platform` and have a public, no-argument
 * constructor. Platform instances are created on first-use of a fluent logger and platform
 * implementors must take care to avoid cycles during initialization and re-entrant behaviour.
 */
public actual abstract class KPlatform actual constructor() {
    protected actual abstract fun getCallerFinderImpl(): KPlatformLogCallerFinder
    protected actual abstract fun getBackendImpl(className: String?): KLoggerBackend
    protected actual open fun getCurrentTimeNanosImpl(): Long {
        TODO("Not yet implemented")
    }

    protected actual abstract fun getConfigInfoImpl(): String

    public actual companion object {

        /**
         * Returns the singleton ContextDataProvider from which a ScopedLoggingContext can be obtained.
         * Platform implementations are required to always provide the same instance here, since this can
         * be cached by callers.
         */
        // TODO: KFlogger public fun getContextDataProvider: ContextDataProvider
        /**
         * Returns the current depth of recursion for logging in the current thread.
         *
         *
         * This method is intended only for use by logging backends or the core Flogger library and
         * only needs to be called by code which is invoking user code which itself might trigger
         * reentrant logging.
         *
         *
         *  * A value of 1 means that this thread is currently in a normal log statement. This is the
         * expected state and the caller should behave normally.
         *  * A value greater than 1 means that this thread is currently performing reentrant logging,
         * and the caller may choose to change behaviour depending on the value if there is a risk that
         * reentrant logging is being caused by the caller's code.
         *  * A value of zero means that this thread is not currently logging (though since this method
         * should only be called as part of a logging library, this is expected to never happen). It
         * should be ignored.
         *
         *
         *
         * When the core Flogger library detects the depth exceeding a preset threshold, it may start
         * to modify its behaviour to attempt to mitigate the risk of unbounded reentrant logging. For
         * example, some or all metadata may be removed from log sites, since processing user provided
         * metadata may itself trigger reentrant logging.
         */
        public actual fun getCurrentRecursionDepth(): Int {
            TODO("Not yet implemented")
        }

        /**
         * Returns the API for obtaining caller information about loggers and logging classes.
         */
        public actual fun getCallerFinder(): KPlatformLogCallerFinder {
            TODO("Not yet implemented")
        }

        /**
         * Returns a logger backend of the given class name for use by a Fluent Logger. Note that the
         * returned backend need not be unique; one backend could be used by multiple loggers. The given
         * class name must be in the normal dot-separated form (e.g. "com.example.Foo$Bar") rather than
         * the internal binary format (e.g. "com/example/Foo$Bar").
         *
         * @param className the fully-qualified name of the Java class to which the logger is associated.
         * The logger name is derived from this string in a platform specific way.
         */
        public actual fun getBackend(className: String): KLoggerBackend {
            TODO("Not yet implemented")
        }

        /**
         * Returns whether the given logger should have logging forced at the specified level. When
         * logging is forced for a log statement it will be emitted regardless or the normal log level
         * configuration of the logger and ignoring any rate limiting or other filtering.
         *
         *
         * This method is intended to be invoked unconditionally from a fluent logger's
         * `at(Level)` method to permit overriding of default logging behavior.
         *
         * @param loggerName the fully qualified logger name (e.g. "com.example.SomeClass")
         * @param level the level of the log statement being invoked
         * @param isEnabled whether the logger is enabled at the given level (i.e. the result of calling
         * `isLoggable()` on the backend instance)
         */
        public actual fun shouldForceLogging(
            loggerName: String?,
            level: KLevel,
            isEnabled: Boolean
        ): Boolean {
            TODO("Not yet implemented")
        }

        /** Returns [KTags] from with the current context to be injected into log statements.  */
        public actual fun getInjectedTags(): KTags {
            TODO("Not yet implemented")
        }

        /** Returns [Metadata] from with the current context to be injected into log statements.  */
        public actual fun getInjectedMetadata(): KMetadata {
            TODO("Not yet implemented")
        }

        /**
         * Returns the current time from the epoch (00:00 1st Jan, 1970) with nanosecond granularity.
         * This is a non-negative signed 64-bit value, which must be in the range `0 <= timestamp
         * < 2^63`, ensuring that the difference between any two timestamps will always yield a valid
         * signed value.
         *
         *
         * Warning: Not all Platform implementations will be able to deliver nanosecond precision and
         * code should avoid relying on any implied precision.
         */
        public actual fun getCurrentTimeNanos(): Long {
            TODO("Not yet implemented")
        }

        /**
         * Returns a human readable string describing the platform and its configuration. This should
         * contain everything a human would need to see to check that the Platform was configured as
         * expected. It should contain the platform name along with any configurable elements
         * (e.g. plugin services) and their settings. It is recommended (though not required) that this
         * string is formatted with one piece of configuration per line in a tabular format, such as:
         * <pre>`platform: <human readable name>
         * formatter: com.example.logging.FormatterPlugin
         * formatter.foo: <"foo" settings for the formatter plugin>
         * formatter.bar: <"bar" settings for the formatter plugin>
        `</pre> *
         * It is not required that this string be machine parseable (though it should be stable).
         */
        public actual fun getConfigInfo(): String {
            TODO("Not yet implemented")
        }

    }
}

/**
 * API for determining the logging class and log statement sites, return from {@link
 * #getCallerFinder}. This classes is immutable and thread safe.
 *
 * <p>This functionality is not provided directly by the {@code Platform} API because doing so
 * would require several additional levels to be added to the stack before the implementation was
 * reached. This is problematic for Android which has only limited stack analysis. By allowing
 * callers to resolve the implementation early and then call an instance directly (this is not an
 * interface), we reduce the number of elements in the stack before the caller is found.
 *
 * <h2>Essential Implementation Restrictions</h2>
 *
 * Any implementation of this API <em>MUST</em> follow the rules listed below to avoid any risk of
 * re-entrant code calling during logger initialization. Failure to do so risks creating complex,
 * hard to debug, issues with Flogger configuration.
 *
 * <ol>
 *   <li>Implementations <em>MUST NOT</em> attempt any logging in static methods or constructors.
 *   <li>Implementations <em>MUST NOT</em> statically depend on any unknown code.
 *   <li>Implementations <em>MUST NOT</em> depend on any unknown code in constructors.
 * </ol>
 *
 * <p>Note that logging and calling arbitrary unknown code (which might log) are permitted inside
 * the instance methods of this API, since they are not called during platform initialization. The
 * easiest way to achieve this is to simply avoid having any non-trivial static fields or any
 * instance fields at all in the implementation.
 *
 * <p>While this sounds onerous it's not difficult to achieve because this API is a singleton, and
 * can delay any actual work until its methods are called. For example if any additional state is
 * required in the implementation, it can be held via a "lazy holder" to defer initialization.
 */
public actual abstract class KPlatformLogCallerFinder {
    /**
     * Returns the name of the immediate caller of the given logger class. This is useful when
     * determining the class name with which to create a logger backend.
     *
     * @param loggerClass the class containing the log() methods whose caller we need to find.
     * @return the name of the class that called the specified logger.
     * @throws IllegalStateException if there was no caller of the specified logged passed on the
     * stack (which may occur if the logger class was invoked directly by JNI).
     */
    public actual abstract fun findLoggingClass(loggerClass: Klass<out KAbstractLogger<*>>): String

    /**
     * Returns a LogSite found from the current stack trace for the caller of the log() method on
     * the given logging class.
     *
     * @param loggerApi the class containing the log() methods whose caller we need to find.
     * @param stackFramesToSkip the number of method calls which exist on the stack between the
     * `log()` method and the point at which this method is invoked.
     * @return A log site inferred from the stack, or [LogSite.INVALID] if no log site can be
     * determined.
     */
    public actual abstract fun findLogSite(loggerApi: Klass<*>?, stackFramesToSkip: Int): KLogSite
}

