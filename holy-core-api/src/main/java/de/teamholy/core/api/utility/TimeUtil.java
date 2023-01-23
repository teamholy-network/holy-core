package de.teamholy.core.api.utility;

import java.util.concurrent.TimeUnit;

/**
 * Util class for time-based operations
 */
public class TimeUtil {

    /**
     * Convert time in specific unit to beauty String
     * Shortified is by default set to true
     *
     * @param time     The time
     * @param timeUnit The TimeUnit
     * @return The converted TimeString
     */
    public static String beautifyTime(long time, TimeUnit timeUnit) {
        return beautifyTime(time, timeUnit, true);
    }

    /**
     * Convert time in specific unit to beauty String
     * and determine if the String should be shorted
     *
     * @param time     The time
     * @param timeUnit The TimeUnit
     * @param shortify true to shortify, false to not
     * @return The converted TimeString
     */
    public static String beautifyTime(long time, TimeUnit timeUnit, boolean shortify) {
        String msg = "";
        long inSeconds = TimeUnit.SECONDS.convert(time, timeUnit);
        if (inSeconds >= 86400L) {
            long days = inSeconds / 86400L;
            msg = msg + days + (shortify ? "d " : ((days == 1L) ? " days, " : " days, "));
            inSeconds %= 86400L;
        }
        if (inSeconds >= 3600L) {
            long hours = inSeconds / 3600L;
            msg = msg + hours + (shortify ? "h " : ((hours == 1L) ? " hour, " : " hours, "));
            inSeconds %= 3600L;
        }
        if (inSeconds >= 60L) {
            long minutes = inSeconds / 60L;
            msg = msg + minutes + (shortify ? "m " : ((minutes == 1L) ? " minute, " : " minutes, "));
            inSeconds %= 60L;
        }
        if (inSeconds > 0L)
            msg = msg + inSeconds + (shortify ? "s " : ((inSeconds == 1L) ? " second, " : " seconds, "));

        if (!msg.isEmpty())
            msg = msg.substring(0, msg.length() - (shortify ? 1 : 2));
        else
            msg = shortify ? "0s" : "0 seconds";
        if (time == -1)
            msg = "permanent";
        return msg;
    }

    /**
     * Convert String to time in millis
     *
     * @param timeString The TimeString
     * @return The time in milliseconds
     */
    public static long getTimeInMilli(String timeString) {
        long multiplier = 0;
        long timeLong = Long.parseLong(timeString.substring(0, timeString.length() - 1));
        multiplier = timeString.endsWith("y") ? 1000 * 60 * 60 * 24 * 7 * 4 * 12L : multiplier;
        multiplier = timeString.endsWith("w") ? 1000 * 60 * 60 * 24 * 7 : multiplier;
        multiplier = timeString.endsWith("d") ? 1000 * 60 * 60 * 24 : multiplier;
        multiplier = timeString.endsWith("h") ? 1000 * 60 * 60 : multiplier;
        multiplier = timeString.endsWith("m") ? 1000 * 60 : multiplier;
        multiplier = timeString.endsWith("s") ? 1000 : multiplier;
        return timeLong * multiplier;
    }


}