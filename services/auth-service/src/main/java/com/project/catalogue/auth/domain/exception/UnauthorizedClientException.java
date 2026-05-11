package com.project.catalogue.auth.domain.exception;

public class UnauthorizedClientException extends RuntimeException {

    public UnauthorizedClientException(String clientId) {
        super("Client not authorized: " + clientId);
    }
}
