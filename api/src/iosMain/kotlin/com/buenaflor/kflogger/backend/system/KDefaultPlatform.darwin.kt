package com.buenaflor.kflogger.backend.system

import com.buenaflor.kflogger.backend.KLoggerBackend
import com.buenaflor.kflogger.backend.KPlatform
import com.buenaflor.kflogger.backend.KPlatformLogCallerFinder

/**
 * The default fluent logger platform for a server-side Java environment.
 *
 * This class allows configuration via a number of service types. A single instance of each service
 * type may be provided, either via the classpath using *service providers* (see [ ]) or by system
 * property. For most users, configuring one of these should just require including the appropriate
 * dependency.
 *
 * If set, the system property for each service type takes precedence over any implementations that
 * may be found on the classpath. The value of the system property is expected to be of one of two
 * forms:
 * * **A fully-qualified class name:** In this case, the platform will attempt to get an instance of
 *   that class by invoking the public no-arg constructor. If the class defines a public static
 *   no-arg `getInstance` method, the platform will call that instead. **Note:** Support for
 *   `getInstance` is only provided to facilitate transition from older service implementations that
 *   include a `getInstance` method and will likely be removed in the future.
 * * **A fully-qualified class name followed by "#" and the name of a static method:** In this case,
 *   the platform will attempt to get an instance of that class by invoking either the named no-arg
 *   static method or the public no-arg constructor. **Note:** This option exists only for
 *   compatibility with previous Flogger behavior and may be removed in the future; service
 *   implementations should prefer providing a no-arg public constructor rather than a static method
 *   and system properties should prefer only including the class name.
 *
 * The services used by this platform are the following:
 * <table>
 * <tr> <th>Service Type</th> <th>System Property</th> <th>Default</th> </tr> *
 * <tr>
 * <td>[BackendFactory]</td>
 * <td>`flogger.backend_factory`</td>
 * <td>[SimpleBackendFactory], a `java.util.logging` backend</td> </tr> *
 * <tr>
 * <td>[ContextDataProvider]</td>
 * <td>`flogger.logging_context`</td>
 * <td>A no-op `ContextDataProvider`</td> </tr> *
 * <tr>
 * <td>[Clock]</td>
 * <td>`flogger.clock`</td>
 * <td>[SystemClock], a millisecond-precision clock</td> </tr> * </table> *
 */
// Non-final for testing.
public actual open class KDefaultPlatform actual constructor() : KPlatform() {
  protected actual override fun getCallerFinderImpl(): KPlatformLogCallerFinder {
    TODO("Not yet implemented")
  }

  protected actual override fun getBackendImpl(className: String?): KLoggerBackend {
    TODO("Not yet implemented")
  }

  protected actual override fun getConfigInfoImpl(): String {
    TODO("Not yet implemented")
  }
}
