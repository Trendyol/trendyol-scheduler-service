package com.trendyol.scheduler.utils;

import org.joda.time.DateTime;

import java.util.Date;

public final class Clock {

    private static boolean isFrozen;

    private static DateTime timeSet;

    private Clock() {
    }

    public static synchronized void freeze() {
        isFrozen = true;
    }

    public static synchronized void freeze(DateTime date) {
        freeze();
        setTime(date);
    }

    public static synchronized void unfreeze() {
        isFrozen = false;
        timeSet = null;
    }

    public static DateTime now() {
        DateTime dateTime = DateTime.now();
        if (isFrozen) {
            if (timeSet == null) {
                timeSet = dateTime;
            }
            return timeSet;
        }

        return dateTime;
    }

    public static synchronized void setTime(DateTime date) {
        timeSet = date;
    }

    public static DateTime toDateTime(Date date) {
        return new DateTime(date);
    }

}
