package com.buenaflor.kflogger

import com.buenaflor.kflogger.backend.KMetadata

/**
 * Status for rate limiting operations, usable by rate limiters and available to subclasses of
 * `LogContext` to handle rate limiting consistently.
 *
 * <h2>Design Notes</h2>
 *
 *
 * The purpose of this class is to allow rate limiters to behave in a way which is consistent
 * when multiple rate limiters are combined for a single log statement. If you are writing a rate
 * limiter for Flogger which you want to "play well" with other rate limiters, it is essential that
 * you understand how `RateLimitStatus` is designed to work.
 *
 *
 * Firstly, `LogContext` tracks a single status for each log statement reached. This is
 * modified by code in the `postProcess()` method (which can be overridden by custom logger
 * implementations).
 *
 *
 * When a rate limiter is used, it returns a `RateLimitStatus`, which is combined with the
 * existing value held in the context:
 *
 * <pre>`rateLimitStatus = RateLimitStatus.combine(rateLimitStatus, MyCustomRateLimiter.check(...));
` * >/pre>
 *
 *
 * A rate limiter should switch between two primary states "limiting" and "pending":
 *
 *  * In the "limiting" state, the limiter should return the [RateLimitStatus.DISALLOW] value
 * and update any internal state until it reaches its trigger condition. Once the trigger condition
 * is reached, the limiter enters the "pending" state.
 *  * In the "pending" state, the limiter returns an "allow" status *until it is
 * [RateLimitStatus.reset]*.
 *
 *
 *
 * This two-step approach means that, when multiple rate limiters are active for a single log
 * statement, logging occurs after all rate limiters are "pending" (and at this point they are all
 * reset). This is much more consistent than having each rate limiter operate independently, and
 * allows a much more intuitive understanding of expected behaviour.
 *
 *
 * It is recommended that most rate limiters should start in the "pending" state to ensure that
 * the first log statement they process is emitted (even when multiple rate limiters are used). This
 * isn't required, but it should be documented either way.
 *
 *
 * Each rate limiter is expected to follow this basic structure:
 *
 * <pre>`final class CustomRateLimiter extends RateLimitStatus {
 * private static final LogSiteMap<CustomRateLimiter> map =
 * new LogSiteMap<CustomRateLimiter>() {
 * protected CustomRateLimiter initialValue() {
 * return new CustomRateLimiter();
 * }
 * };
 *
 * static RateLimitStatus check(Metadata metadata, LogSiteKey logSiteKey, ...) {
 * MyRateLimitData rateLimitData = metadata.findValue(MY_CUSTOM_KEY);
 * if (rateLimitData == null) {
 * return null;
 * }
 * return map.get(logSiteKey, metadata).checkRateLimit(rateLimitData, ...);
 * }
 *
 * RateLimitStatus checkRateLimit(MyRateLimitData rateLimitData, ...) {
 * <update internal state>
 * return <is-pending> ? this : DISALLOW;
 * }
 *
 *
 * public void reset() {
 * <reset from "pending" to "limiting" state>
 * }
 * }
` * >/pre>
 *
 *
 * The use of `LogLevelMap` ensures a rate limiter instance is held separately for each log
 * statement, but it also handles complex garbage collection issues around "specialized" log site
 * keys. All rate limiter implementations *MUST* use this approach.
 *
 *
 * Having the rate limiter class extend `RateLimitStatus` is a convenience for the case
 * where the `reset()` operation requires no additional information. If the `reset()`
 * operation requires extra state (e.g. from previous logging calls) then this approach will not be
 * possible, and a separate `RateLimitStatus` subclass would need to be allocated to hold that
 * state.
 *
 *
 * Rate limiter instances *MUST* be thread safe, and should avoid using locks wherever
 * possible (since using explicit locking can cause unacceptable thread contention in highly
 * concurrent systems).
</pre></pre> */
public expect abstract class KRateLimitStatus
/**
 * Rate limiters can extend this class directly if their "reset" operation is stateless, or they
 * can create and return new instances to capture any necessary state.
 */
protected constructor() {
    /**
     * Resets an associated rate limiter, moving it out of the "pending" state and back into rate
     * limiting mode.
     *
     *
     * Note: This method is never invoked concurrently with another `reset()` operation, but
     * it can be concurrent with calls to update rate limiter state. Thus it must be thread safe in
     * general, but can assume it's the only reset operation active for the limiter which returned it.
     */
    protected abstract fun reset()

    public companion object {
        /**
         * The status to return whenever a rate limiter determines that logging should not occur.
         *
         *
         * All other statuses implicity "allow" logging.
         */
        public val DISALLOW: KRateLimitStatus

        /**
         * The status to return whenever a stateless rate limiter determines that logging should occur.
         *
         *
         * Note: Truly stateless rate limiters should be *very* rare, since they cannot hold
         * onto a pending "allow" state. Even a simple "sampling rate limiter" should be stateful if once
         * the "allow" state is reached it continues to be returned until logging actually occurs.
         */
        public val ALLOW: KRateLimitStatus

        /**
         * The rules for combining statuses are (in order):
         *
         *
         *  * If either value is `null`, the other value is returned (possibly `null`).
         *  * If either value is `ALLOW` (the constant), the other non-null value is returned.
         *  * If either value is `DISALLOW`, `DISALLOW` is returned.
         *  * Otherwise a combined status is returned from the two non-null "allow" statuses.
         *
         *
         *
         * In [LogContext] the `rateLimitStatus` field is set to the combined value of all
         * rate limiter statuses.
         *
         *
         * This ensures that after rate limit processing:
         *
         *
         *  1. If `rateLimitStatus == null` no rate limiters were applied, so logging is allowed.
         *  1. If `rateLimitStatus == DISALLOW`, the log was suppressed by rate limiting.
         *  1. Otherwise the log statement was allowed, but rate limiters must now be reset.
         *
         *
         *
         * This code ensures that in the normal case of having no rate limiting for a log statement, no
         * allocations occur. It also ensures that (assuming well written rate limiters) there are no
         * allocations for log statements using a single rate limiter.
         */
        public fun combine(a: KRateLimitStatus?, b: KRateLimitStatus?): KRateLimitStatus?

        /**
         * Checks rate limiter status and returns either the number of skipped log statements for the
         * `logSiteKey` (indicating that this log statement should be emitted) or `-1` if it
         * should be skipped.
         */
        public fun checkStatus(
            status: KRateLimitStatus,
            logSiteKey: KLogSiteKey?,
            metadata: KMetadata?
        ): Int
    }
}
