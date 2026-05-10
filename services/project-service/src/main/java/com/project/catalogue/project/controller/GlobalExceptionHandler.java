package com.project.catalogue.project.controller;

import com.project.catalogue.project.domain.exception.ProjectAlreadyExistsException;
import com.project.catalogue.project.domain.exception.ProjectNotFoundException;
import com.project.catalogue.project.domain.exception.ProjectUserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleProjectNotFound(ProjectNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND, "PROJECT_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(ProjectUserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleProjectUserNotFound(ProjectUserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND, "PROJECT_USER_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(ProjectAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleProjectAlreadyExists(ProjectAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT, "PROJECT_ALREADY_EXISTS", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.validationError(HttpStatus.BAD_REQUEST, "Validation failed", errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT, "PROJECT_ALREADY_EXISTS", "This project is already associated with this user."));
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ApiResponse<Void>> handleRestClientError(RestClientResponseException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(HttpStatus.SERVICE_UNAVAILABLE, "UPSTREAM_ERROR", "Could not reach user-service — please try again later."));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex.getMessage()));
    }
}
