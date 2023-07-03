package com.buenaflor.kflogger

/**
 * A tagging interface to mark implementations that are suitable for use as a key for looking up per
 * log site persistent state. Normally the class used is just {@link LogSite} but other, more
 * specific, keys can be used. There are no method requirements on this interface, but the instance
 * must have correct {@code equals()}, {@code hashCode()} and {@code toString()} implementations and
 * must be at least as unique as the associated {@code LogSite} (i.e. two keys created for different
 * log sites must never be equal).
 */
actual interface KLogSiteKey
