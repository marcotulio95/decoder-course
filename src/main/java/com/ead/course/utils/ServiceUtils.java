package com.ead.course.utils;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public class ServiceUtils {
    private ServiceUtils() {
    }

    public static String createUrlGetAllUsersByCourse(UUID courseId, Pageable pageable) {
        return "/users?courseId=" + courseId + "&page=" + pageable.getPageNumber() + "&size="
            + pageable.getPageSize() + "&sort=" + pageable.getSort().toString().replaceAll(": ", ",");
    }
}
