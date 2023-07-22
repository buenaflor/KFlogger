import com.buenaflor.kflogger.KFluentLogger

/** Integration (actually build) test for source compatibility for usages of FluentLogger. */
@Suppress("unused")
fun sourceCompatibility() {
  val logger = KFluentLogger.forEnclosingClass()
  logger.atWarning().log("test")
}
