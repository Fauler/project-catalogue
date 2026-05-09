package com.project.catalogue.project.domain;

public class ProjectUserNotFoundException extends RuntimeException {

    public ProjectUserNotFoundException(Long userId) {
        super("User not found for project association. userId=" + userId);
    }
}

