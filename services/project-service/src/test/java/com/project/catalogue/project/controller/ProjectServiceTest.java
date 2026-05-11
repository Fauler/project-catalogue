package com.project.catalogue.project.controller;

import com.project.catalogue.project.boundary.ProjectRequest;
import com.project.catalogue.project.boundary.ProjectResponse;
import com.project.catalogue.project.domain.exception.ProjectAlreadyExistsException;
import com.project.catalogue.project.domain.exception.ProjectNotFoundException;
import com.project.catalogue.project.domain.exception.ProjectUserNotFoundException;
import com.project.catalogue.project.domain.model.Project;
import com.project.catalogue.project.domain.repository.ProjectRepository;
import com.project.catalogue.project.domain.repository.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository repository;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private ProjectService service;

    private Project project;
    private ProjectRequest request;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setUserId(10L);
        project.setName("My Project");
        project.setLocation("github.com/user/repo");
        project.setCreatedAt(LocalDateTime.now());

        request = new ProjectRequest("My Project", "github.com/user/repo");
    }

    @Test
    void addProjectToUser_success() {
        // given
        when(repository.findByUserIdAndLocation(10L, request.location())).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(project);

        // when
        ProjectResponse response = service.addProjectToUser(10L, request);

        // then
        assertThat(response.getName()).isEqualTo("My Project");
        assertThat(response.getUserId()).isEqualTo(10L);
    }

    @Test
    void addProjectToUser_userNotFound_throws() {
        // given
        doThrow(new ProjectUserNotFoundException(99L)).when(userValidator).validateUserExists(99L);

        // when / then
        assertThatThrownBy(() -> service.addProjectToUser(99L, request))
                .isInstanceOf(ProjectUserNotFoundException.class);
    }

    @Test
    void addProjectToUser_duplicate_throws() {
        // given
        when(repository.findByUserIdAndLocation(10L, request.location())).thenReturn(Optional.of(project));

        // when / then
        assertThatThrownBy(() -> service.addProjectToUser(10L, request))
                .isInstanceOf(ProjectAlreadyExistsException.class);
    }

    @Test
    void listProjectsByUser_success() {
        // given
        Page<Project> page = new PageImpl<>(List.of(project));
        when(repository.findByUserId(eq(10L), any(Pageable.class))).thenReturn(page);

        // when
        Page<ProjectResponse> result = service.listProjectsByUser(10L, 0, 10);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("My Project");
    }

    @Test
    void removeProject_success() {
        // given
        when(repository.findById(1L)).thenReturn(Optional.of(project));

        // when
        service.removeProject(10L, 1L);

        // then
        verify(repository).deleteById(1L);
    }

    @Test
    void removeProject_notFound_throws() {
        // given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> service.removeProject(10L, 99L))
                .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void removeProject_wrongUser_throws() {
        // given
        project.setUserId(99L);
        when(repository.findById(1L)).thenReturn(Optional.of(project));

        // when / then
        assertThatThrownBy(() -> service.removeProject(10L, 1L))
                .isInstanceOf(ProjectNotFoundException.class);
    }
}
