package com.buenaflor.kflogger.backend.system

import com.buenaflor.kflogger.backend.KLoggerBackend

/**
 * An API to create logger backends for a given class name. This is implemented as an abstract class
 * (rather than an interface) to reduce to risk of breaking existing implementations if the API
 * changes.
 *
 * <h2>Essential Implementation Restrictions</h2>
 *
 * Any implementation of this API *MUST* follow the rules listed below to avoid any risk of
 * re-entrant code calling during logger initialization. Failure to do so risks creating complex,
 * hard to debug, issues with Flogger configuration.
 * 1. Implementations *MUST NOT* attempt any logging in static methods or constructors.
 * 1. Implementations *MUST NOT* statically depend on any unknown code.
 * 1. Implementations *MUST NOT* depend on any unknown code in constructors.
 *
 * Note that logging and calling arbitrary unknown code (which might log) are permitted inside the
 * instance methods of this API, since they are not called during platform initialization. The
 * easiest way to achieve this is to simply avoid having any non-trivial static fields or any
 * instance fields at all in the implementation.
 *
 * While this sounds onerous it's not difficult to achieve because this API is a singleton, and can
 * delay any actual work until its methods are called. For example if any additional state is
 * required in the implementation, it can be held via a "lazy holder" to defer initialization.
 */
public actual abstract class KBackendFactory {
  /**
   * Creates a logger backend of the given class name for use by a Fluent Logger. Note that the
   * returned backend need not be unique; one backend could be used by multiple loggers. The given
   * class name must be in the normal dot-separated form (e.g., "com.example.Foo$Bar") rather than
   * the internal binary format "com/example/Foo$Bar").
   *
   * @param loggingClassName the fully-qualified name of the Java class to which the logger is
   *   associated. The logger name is derived from this string in a backend specific way.
   */
  public actual abstract fun create(loggingClassName: String): KLoggerBackend
}
