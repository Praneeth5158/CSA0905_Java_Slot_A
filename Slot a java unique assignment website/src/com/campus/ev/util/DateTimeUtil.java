package com.campus.ev.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    private static final SimpleDateFormat SDF_DATETIME = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat SDF_TIME = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat SDF_TIME_SHORT = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat SDF_PRETTY = new SimpleDateFormat("dd MMM yyyy, hh:mm a");

    public static String formatDateTime(Timestamp ts) {
        if (ts == null) return "-";
        return SDF_DATETIME.format(ts);
    }

    public static String formatDate(Date date) {
        if (date == null) return "-";
        return SDF_DATE.format(date);
    }

    public static String formatTime(Date date) {
        if (date == null) return "-";
        return SDF_TIME.format(date);
    }

    public static String formatTimeShort(Date date) {
        if (date == null) return "-";
        return SDF_TIME_SHORT.format(date);
    }

    public static String formatPretty(Timestamp ts) {
        if (ts == null) return "-";
        return SDF_PRETTY.format(ts);
    }

    public static String formatDuration(int totalMinutes) {
        if (totalMinutes <= 0) return "00:00:00";
        int hrs = totalMinutes / 60;
        int mins = totalMinutes % 60;
        return String.format("%02d:%02d:00", hrs, mins);
    }

    public static String formatDurationSeconds(long totalSeconds) {
        if (totalSeconds <= 0) return "00:00:00";
        long hrs = totalSeconds / 3600;
        long mins = (totalSeconds % 3600) / 60;
        long secs = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hrs, mins, secs);
    }
}
