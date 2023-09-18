# KFlogger: KMP port of [Flogger](https://github.com/google/flogger)

## Supported Platforms

| Target Platform | Target preset                                                                                                |
|:---------------:|--------------------------------------------------------------------------------------------------------------|
|   Kotlin/JVM    | <ul><li>`jvm`</li></ul>                                                                                      
|       iOS       | <ul><li>`iosArm64`</li><li>`iosX64`</li><li>`iosSimulatorArm64`</li></ul>                                    |

## Install

```kotlin
// build.gradle.kts

implementation("com.giancarlobuenaflor:kflogger:<version>")
```

## Usage

```kotlin
import com.giancarlobuenaflor.kflogger.KFluentLogger

class LoggingClass {
  val logger = KFluentLogger.forEnclosingClass()

  fun loggingMethod() {
    logger.atWarning().log("my message: %s", "Hello World")
  }
}
```


## Current state as of commit e724eeba1fd440b20bf8e865a1d735a0dd238ec0

### Common functionality

- logging simple messages without arguments at all log levels -> `log.atWarning().log("my message")` or `log.atFine().log("my message")`
- logging messages with printf format -> `log.atWarning("my message: $s", "test")` although this is only limited to `%s, %d, %i, %f% due to lack of implementation on the iOS side.
- avoid work at log site with lazy -> `log.atWarning("complex work: $s", KLazyArgs.lazy { "result" })`

### Implementation approach

We use `actual typealias` very extensively to make sure we can keep the original Flogger Java code.
This has some weird caveats, for example Java protected does not equal Kotlin protected or Kotlin companion functions cannot be typealiased to static functions on Java.
The workaround here is to ignore those errors and make sure compilation works through tests.

### JVM Flogger

The core functionality will work as usual on JVM.

Most of the code has been directly transferred from [Flogger](https://github.com/google/flogger/). Changes to the Flogger API to accomodate KMP are very minimal and are the following:
 - [Change modifier of `parsePrintfTerm` to protected](https://github.com/buenaflor/KFlogger/pull/13#discussion_r1283879010)
 - [Add functionality to `isTargetClass`](https://github.com/buenaflor/KFlogger/pull/12#discussion_r1283877056)

Currently only the [api module](https://github.com/google/flogger/tree/master/api) is available in KMP but adding the other ones should not be an issue since the `api` module is already incorporated into KMP.

### iOS

The iOS implementation has a lot of gaps and many TODOs.

It only contains the core implementations necessary to make the common functionality work. 

- [OSLog](https://developer.apple.com/documentation/oslog) is used as default logging backend.
- The printf format is done manually inside `SimpleLoggerBackend`. This is less than ideal and should be properly implemented via `PrintfMessageParser` and the `BaseMessageFormatter` and other relevant classes.
- `findLoggingClass` has been implemented with `NSThread.callStackSymbols`. This implementation assumes that the logging class is within the common Kotlin code. So it won't work for native Obj-c or Swift classes.

### Testing

JVM Flogger contains the original test cases.

The common code is tested via [Kruth](https://github.com/androidx/androidx/tree/androidx-main/kruth/kruth).
The approach here is to take the JVM Flogger tests 1:1 and apply them to Kruth to make sure functionality is consistent across all platforms.

In addition to ensure the `actual typealias` work for all defined common code we test compilation of all KMP classes to make sure we don't run into any runtime errors.
