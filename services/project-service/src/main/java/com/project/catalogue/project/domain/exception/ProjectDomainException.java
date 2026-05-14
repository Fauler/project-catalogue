package com.project.catalogue.project.domain.exception;

public sealed class ProjectDomainException extends RuntimeException
        permits ProjectNotFoundException, ProjectAlreadyExistsException, ProjectUserNotFoundException {

    public ProjectDomainException(String message) {
        super(message);
    }
}

