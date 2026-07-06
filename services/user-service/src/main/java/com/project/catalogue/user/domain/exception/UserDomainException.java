package com.project.catalogue.user.domain.exception;

public sealed class UserDomainException extends RuntimeException
        permits UserNotFoundException, UserAlreadyExistsException, InvalidCredentialsException {

    public UserDomainException(String message) {
        super(message);
    }
}

