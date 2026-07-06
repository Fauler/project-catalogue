package com.project.catalogue.project.domain.repository;

import com.project.catalogue.project.domain.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByUserId(Long userId, Pageable pageable);

    Optional<Project> findByUserIdAndLocation(Long userId, String location);

    long deleteByUserId(Long userId);
}

