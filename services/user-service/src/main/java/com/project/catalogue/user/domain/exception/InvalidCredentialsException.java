package com.project.catalogue.user.domain.exception;

public final class InvalidCredentialsException extends UserDomainException {
    public InvalidCredentialsException(String email) {
        super("Invalid credentials for user " + email);
    }
}
