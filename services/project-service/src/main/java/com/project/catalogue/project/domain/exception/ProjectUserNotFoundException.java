package com.project.catalogue.project.domain.exception;

public final class ProjectUserNotFoundException extends ProjectDomainException {
    public ProjectUserNotFoundException(Long userId) {
        super("User " + userId + " does not exist");
    }
}

