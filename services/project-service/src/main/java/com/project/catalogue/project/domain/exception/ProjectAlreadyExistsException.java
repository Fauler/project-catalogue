package com.project.catalogue.project.domain.exception;

public class ProjectAlreadyExistsException extends RuntimeException {
    public ProjectAlreadyExistsException(String location) {
        super("Project already exists at location: " + location);
    }
}

