package com.project.catalogue.project.domain.exception;

public final class ProjectAlreadyExistsException extends ProjectDomainException {
    public ProjectAlreadyExistsException(String location) {
        super("Project already exists at location: " + location);
    }
}

