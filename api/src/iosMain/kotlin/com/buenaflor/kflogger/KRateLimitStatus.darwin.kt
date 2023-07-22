package com.buenaflor.kflogger

import com.buenaflor.kflogger.backend.KMetadata

public actual abstract class KRateLimitStatus {
    /**
     * Resets an associated rate limiter, moving it out of the "pending" state and back into rate
     * limiting mode.
     *
     *
     * Note: This method is never invoked concurrently with another `reset()` operation, but
     * it can be concurrent with calls to update rate limiter state. Thus it must be thread safe in
     * general, but can assume it's the only reset operation active for the limiter which returned it.
     */
    protected actual abstract fun reset()

    public actual companion object {
        /**
         * The status to return whenever a rate limiter determines that logging should not occur.
         *
         *
         * All other statuses implicity "allow" logging.
         */
        public actual val DISALLOW: KRateLimitStatus
            get() = TODO("Not yet implemented")

        /**
         * The status to return whenever a stateless rate limiter determines that logging should occur.
         *
         *
         * Note: Truly stateless rate limiters should be *very* rare, since they cannot hold
         * onto a pending "allow" state. Even a simple "sampling rate limiter" should be stateful if once
         * the "allow" state is reached it continues to be returned until logging actually occurs.
         */
        public actual val ALLOW: KRateLimitStatus
            get() = TODO("Not yet implemented")

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
        public actual fun combine(
            a: KRateLimitStatus?,
            b: KRateLimitStatus?
        ): KRateLimitStatus? {
            TODO("Not yet implemented")
        }

        /**
         * Checks rate limiter status and returns either the number of skipped log statements for the
         * `logSiteKey` (indicating that this log statement should be emitted) or `-1` if it
         * should be skipped.
         */
        public actual fun checkStatus(
            status: KRateLimitStatus,
            logSiteKey: KLogSiteKey?,
            metadata: KMetadata?
        ): Int {
            TODO("Not yet implemented")
        }

    }

}