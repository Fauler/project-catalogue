package com.project.catalogue.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.catalogue.project.boundary.ProjectRequest;
import com.project.catalogue.project.boundary.ProjectResponse;
import com.project.catalogue.project.domain.exception.ProjectNotFoundException;
import com.project.catalogue.project.domain.exception.ProjectUserNotFoundException;
import com.project.catalogue.project.infrastructure.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private JwtService jwtService;

    private ProjectResponse sampleResponse() {
        return new ProjectResponse(1L, 10L, "My Project", "github.com/user/repo", LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addProject_validPayload_returns201() throws Exception {
        // given
        when(projectService.addProjectToUser(eq(10L), any())).thenReturn(sampleResponse());
        ProjectRequest request = new ProjectRequest("My Project", "github.com/user/repo");

        // when / then
        mockMvc.perform(post("/api/v1/users/10/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.name").value("My Project"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addProject_missingName_returns400() throws Exception {
        // given
        ProjectRequest request = new ProjectRequest("", "github.com/user/repo");

        // when / then
        mockMvc.perform(post("/api/v1/users/10/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(roles = "USER")
    void listProjects_success_returns200() throws Exception {
        // given
        when(projectService.listProjectsByUser(eq(10L), eq(0), eq(10)))
                .thenReturn(new PageImpl<>(List.of(sampleResponse())));

        // when / then
        mockMvc.perform(get("/api/v1/users/10/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("My Project"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeProject_success_returns200() throws Exception {
        // given

        // when / then
        mockMvc.perform(delete("/api/v1/users/10/projects/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeProject_notFound_returns404() throws Exception {
        // given
        doThrow(new ProjectNotFoundException("99")).when(projectService).removeProject(10L, 99L);

        // when / then
        mockMvc.perform(delete("/api/v1/users/10/projects/99").with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("PROJECT_NOT_FOUND"));
    }

    @Test
    void listProjects_noAuth_returns401() throws Exception {
        // given

        // when / then
        mockMvc.perform(get("/api/v1/users/10/projects"))
                .andExpect(status().isUnauthorized());
    }
}
