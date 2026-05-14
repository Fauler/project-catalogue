package com.project.catalogue.project.domain.exception;

public final class ProjectNotFoundException extends ProjectDomainException {
    public ProjectNotFoundException(String projectId) {
        super("No project with id " + projectId);
    }
}

