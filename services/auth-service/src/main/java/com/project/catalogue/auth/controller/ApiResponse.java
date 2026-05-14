package com.project.catalogue.auth.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        int status,
        T data,
        ApiError error
) {
    public static <T> ApiResponse<T> success(HttpStatus status, T data) {
        return new ApiResponse<>(status.value(), data, null);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String code, String message) {
        return new ApiResponse<>(status.value(), null, new ApiError(code, message, null, LocalDateTime.now()));
    }

    public static <T> ApiResponse<T> validationError(HttpStatus status, String message, Map<String, String> details) {
        return new ApiResponse<>(
                status.value(),
                null,
                new ApiError("VALIDATION_ERROR", message, details, LocalDateTime.now())
        );
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ApiError(
            String code,
            String message,
            Map<String, String> details,
            LocalDateTime timestamp
    ) {}
}
