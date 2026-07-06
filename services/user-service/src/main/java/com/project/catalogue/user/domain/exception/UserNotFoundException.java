package com.project.catalogue.user.domain.exception;

public final class UserNotFoundException extends UserDomainException {
    public UserNotFoundException(Long id) {
        super("No user with id " + id);
    }

    public UserNotFoundException(String email) {
        super("No user with email " + email);
    }
}

