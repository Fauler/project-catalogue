package com.project.catalogue.project.controller;

import com.project.catalogue.project.domain.ProjectAlreadyExistsException;
import com.project.catalogue.project.domain.ProjectNotFoundException;
import com.project.catalogue.project.domain.ProjectUserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProjectNotFound(ProjectNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(ProjectUserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProjectUserNotFound(ProjectUserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(ProjectAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleProjectAlreadyExists(ProjectAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.ofValidation(HttpStatus.BAD_REQUEST, "Validation failed", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    record ApiErrorResponse(
            int status,
            String error,
            String message,
            Map<String, String> validationErrors,
            LocalDateTime timestamp
    ) {
        static ApiErrorResponse of(HttpStatus status, String message) {
            return new ApiErrorResponse(
                    status.value(),
                    status.getReasonPhrase(),
                    message,
                    null,
                    LocalDateTime.now()
            );
        }

        static ApiErrorResponse ofValidation(HttpStatus status, String message, Map<String, String> validationErrors) {
            return new ApiErrorResponse(
                    status.value(),
                    status.getReasonPhrase(),
                    message,
                    validationErrors,
                    LocalDateTime.now()
            );
        }
    }
}

