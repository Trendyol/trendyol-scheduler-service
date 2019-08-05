package com.trendyol.scheduler.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.util.Assert;

public final class DateUtils {

    private static final String DATE_AND_TIME_FORMAT_SLASH = "dd/MM/yyyy HH:mm";
    private static final String DATE_AND_TIME_FORMAT_WITH_SECOND_SLASH = "dd/MM/yyyy HH:mm:ss";

    private DateUtils() {
    }

    public static DateTime toDateTime(String dateAsString) {
        Assert.notNull(dateAsString, "dateAsString cannot be null");
        return DateTime.parse(dateAsString, DateTimeFormat.forPattern(DATE_AND_TIME_FORMAT_SLASH));
    }

    public static DateTime toDateTimeWithSecond(String dateAsString) {
        Assert.notNull(dateAsString, "dateAsString cannot be null");
        return DateTime.parse(dateAsString, DateTimeFormat.forPattern(DATE_AND_TIME_FORMAT_WITH_SECOND_SLASH));
    }

}
