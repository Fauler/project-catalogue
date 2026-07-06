package com.project.catalogue.project.domain.exception;

public final class ProjectNotFoundException extends ProjectDomainException {
    public ProjectNotFoundException(Long projectId) {
        super("No project with id " + projectId);
    }
}

