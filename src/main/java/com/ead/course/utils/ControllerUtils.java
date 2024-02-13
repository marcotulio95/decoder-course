package com.ead.course.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class ControllerUtils {
    public static ResponseEntity<Object> notFound(final UUID uuid) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("[%s] not found", uuid));
    }

    public static ResponseEntity<Object> conflict(final UUID uuid, final String message) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(String.format("%s %s", uuid, message));
    }
}
