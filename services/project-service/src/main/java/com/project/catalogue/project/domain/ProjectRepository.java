package com.project.catalogue.project.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<ExternalProject, String> {

    List<ExternalProject> findByUserId(Long userId);
}

