package com.project.catalogue.auth.domain.exception;

public class InvalidUserCredentialsException extends RuntimeException {

    public InvalidUserCredentialsException(String email) {
        super("Invalid credentials for user: " + email);
    }
}
