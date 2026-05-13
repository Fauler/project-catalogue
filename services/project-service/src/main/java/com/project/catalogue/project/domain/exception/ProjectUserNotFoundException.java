package com.project.catalogue.project.domain.exception;

public class ProjectUserNotFoundException extends RuntimeException {
    public ProjectUserNotFoundException(Long userId) {
        super("User " + userId + " does not exist");
    }
}

