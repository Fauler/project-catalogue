package com.project.catalogue.user.domain.exception;

public final class UserAlreadyExistsException extends UserDomainException {
    public UserAlreadyExistsException(String email) {
        super("Email already taken: " + email);
    }
}

