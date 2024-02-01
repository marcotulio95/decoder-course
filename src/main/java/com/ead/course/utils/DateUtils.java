package com.ead.course.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtils {

    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static LocalDateTime getLocalDateTimeNow() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }
}
