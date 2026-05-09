package com.project.catalogue.project.domain;

public class ProjectAlreadyExistsException extends RuntimeException {

    public ProjectAlreadyExistsException(String projectId) {
        super("Project already exists with id: " + projectId);
    }
}

