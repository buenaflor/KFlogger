import com.buenaflor.kflogger.FluentLogger

/**
 * Integration (actually build) test for source compatibility for usages of ArraySet.
 */
@Suppress("unused")
fun sourceCompatibility() {
    val logger = FluentLogger.forEnclosingClass()
    logger.atWarning().log("test")
}