package com.project.catalogue.project.controller;

import com.project.catalogue.project.boundary.ProjectResponse;
import com.project.catalogue.project.infrastructure.JwtProperties;
import com.project.catalogue.project.infrastructure.JwtService;
import com.project.catalogue.project.infrastructure.SecurityConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "jwt.secret=bG9jYWwtZGV2LXNlY3JldC1rZXktZm9yLXByb2plY3QtY2F0YWxvZ3VlLXBvYw=="
})
class ProjectSecurityTest {

    private static final String TEST_SECRET =
            "bG9jYWwtZGV2LXNlY3JldC1rZXktZm9yLXByb2plY3QtY2F0YWxvZ3VlLXBvYw==";

    private static final String TAMPERED_BEARER =
            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWNrZXJAZXZpbC5jb20ifQ.BADSIG";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private JwtService jwtService;

    @BeforeEach
    void setupJwtDelegate() {
        JwtService real = new JwtService(new JwtProperties(TEST_SECRET));
        lenient().when(jwtService.validateToken(any()))
                .thenAnswer(inv -> real.validateToken(inv.getArgument(0)));
    }

    private String expiredBearer() {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET));
        return "Bearer " + Jwts.builder()
                .subject("client-admin-01")
                .claim("role", "ADMIN")
                .issuedAt(new Date(System.currentTimeMillis() - 7_200_000))
                .expiration(new Date(System.currentTimeMillis() - 3_600_000))
                .signWith(key)
                .compact();
    }

    private ProjectResponse sampleProject() {
        return new ProjectResponse(1L, 1L, "My Project", "github.com/user/repo", LocalDateTime.now());
    }

    // -- 401: no token --

    @Test
    void listProjects_noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/1/projects"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addProject_noToken_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/users/1/projects").with(csrf())
                        .contentType("application/json")
                        .content("{\"name\":\"My Project\",\"location\":\"github.com/user/repo\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteProject_noToken_returns401() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1/projects/1").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    // -- 401: bad token --

    @Test
    void listProjects_tamperedToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/1/projects")
                        .header("Authorization", TAMPERED_BEARER))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listProjects_expiredToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/1/projects")
                        .header("Authorization", expiredBearer()))
                .andExpect(status().isUnauthorized());
    }

    // -- 403: wrong role --

    @Test
    @WithMockUser(roles = "USER")
    void addProject_roleUser_returns403() throws Exception {
        mockMvc.perform(post("/api/v1/users/1/projects").with(csrf())
                        .contentType("application/json")
                        .content("{\"name\":\"My Project\",\"location\":\"github.com/user/repo\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteProject_roleUser_returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1/projects/1").with(csrf()))
                .andExpect(status().isForbidden());
    }

    // -- 200: correct role --

    @Test
    @WithMockUser(roles = "USER")
    void listProjects_roleUser_returns200() throws Exception {
        when(projectService.listProjectsByUser(1L, 0, 10))
                .thenReturn(new PageImpl<>(List.of(sampleProject())));
        mockMvc.perform(get("/api/v1/users/1/projects"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addProject_roleAdmin_returns201() throws Exception {
        when(projectService.addProjectToUser(any(), any())).thenReturn(sampleProject());
        mockMvc.perform(post("/api/v1/users/1/projects").with(csrf())
                        .contentType("application/json")
                        .content("{\"name\":\"My Project\",\"location\":\"github.com/user/repo\"}"))
                .andExpect(status().isCreated());
    }
}
