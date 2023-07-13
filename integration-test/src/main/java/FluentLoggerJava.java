import com.buenaflor.kflogger.FluentLogger;

/**
 * Integration (actually build) test for source compatibility for usages of FluentLogger.
 */
public class FluentLoggerJava {
    @SuppressWarnings("unused")
    public static void sourceCompatibility() {
        FluentLogger logger = FluentLogger.forEnclosingClass();
        logger.atWarning().log("test");
    }
}
