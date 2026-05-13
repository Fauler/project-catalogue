package com.project.catalogue.auth.controller;

import com.project.catalogue.auth.infrastructure.AuthProperties;
import com.project.catalogue.auth.infrastructure.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(com.project.catalogue.auth.infrastructure.SecurityConfig.class)
@EnableConfigurationProperties(AuthProperties.class)
@TestPropertySource(properties = {
        "jwt.secret=dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3Rz",
        "jwt.expiration-ms=3600000",
        "auth.allowed-client-ids=client-admin-01,client-user-01",
        "auth.admin-client-ids=client-admin-01"
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    // ── 200: known admin client ──

    @Test
    void token_adminClient_returns200WithToken() throws Exception {
        when(jwtService.generateToken("client-admin-01", "ADMIN")).thenReturn("jwt-admin-token");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);

        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientId\": \"client-admin-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.token").value("jwt-admin-token"))
                .andExpect(jsonPath("$.data.expiresIn").value(3600))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    // ── 200: known user client (non-admin) ──

    @Test
    void token_userClient_returns200WithUserRole() throws Exception {
        when(jwtService.generateToken("client-user-01", "USER")).thenReturn("jwt-user-token");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);

        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientId\": \"client-user-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("jwt-user-token"));
    }

    // ── 401: unknown client ──

    @Test
    void token_unknownClient_returns401() throws Exception {
        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientId\": \"unknown-client\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED_CLIENT"));
    }

    // ── 400: empty payload / missing clientId ──

    @Test
    void token_emptyPayload_returns400() throws Exception {
        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    @Test
    void token_blankClientId_returns400() throws Exception {
        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientId\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    // ── Security: only /auth/token and /actuator/health are allowed ──

    @Test
    void anyOtherEndpoint_returns403or401() throws Exception {
        mockMvc.perform(get("/anything-else"))
                .andExpect(status().isForbidden());
    }
}

