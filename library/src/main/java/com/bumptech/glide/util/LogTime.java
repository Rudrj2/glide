package com.bumptech.glide.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.SystemClock;

/**
 * A class for logging elapsed real time in millis.
 */
public class LogTime {
    private static final double MILLIS_MULTIPLIER = Build.VERSION.SDK_INT >= 17 ? (1d / Math.pow(10, 6)) : 1d;

    /**
     * Returns the current time in either millis or nanos depending on the api level to be used with
     * {@link #getElapsedMillis(long)}.
     */
    @TargetApi(17)
    public static long getLogTime() {
        if (Build.VERSION.SDK_INT >= 17) {
            return SystemClock.elapsedRealtimeNanos();
        } else {
            return System.currentTimeMillis();
        }
    }

    /**
     * Returns the time elapsed since the given logTime in millis.
     *
     * @param logTime The start time of the event.
     */
    public static double getElapsedMillis(long logTime) {
        return (getLogTime() - logTime) * MILLIS_MULTIPLIER;
    }
}