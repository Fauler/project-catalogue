package com.project.catalogue.user.domain.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("Email already taken: " + email);
    }
}

