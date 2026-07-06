package com.project.catalogue.user.controller;

import tools.jackson.databind.ObjectMapper;
import com.project.catalogue.user.boundary.UserRequest;
import com.project.catalogue.user.boundary.CredentialsRequest;
import com.project.catalogue.user.boundary.UserResponse;
import com.project.catalogue.user.boundary.UserService;
import com.project.catalogue.user.domain.exception.InvalidCredentialsException;
import com.project.catalogue.user.domain.exception.UserNotFoundException;
import com.project.catalogue.user.infrastructure.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    private UserResponse sampleResponse() {
        return new UserResponse(1L, "john@example.com", "John", "Doe", LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "USER")
    void returns201_withValidUserPayload() throws Exception {
        // given
        when(userService.createUser(any())).thenReturn(sampleResponse());
        UserRequest request = new UserRequest("john@example.com", "John", "Doe", "secret123");

        // when / then
        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.email").value("john@example.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void returns400_whenEmailIsInvalid() throws Exception {
        // given
        UserRequest request = new UserRequest("not-an-email", "John", "Doe", "secret123");

        // when / then
        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(roles = "USER")
    void returns200_withUserData() throws Exception {
        // given
        when(userService.getUser(1L)).thenReturn(sampleResponse());

        // when / then
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void returns404_whenUserDoesNotExist() throws Exception {
        // given
        when(userService.getUser(99L)).thenThrow(new UserNotFoundException(99L));

        // when / then
        mockMvc.perform(get("/api/v1/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void returns204_onSuccessfulDelete() throws Exception {
        // given / when / then
        mockMvc.perform(delete("/api/v1/users/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void returns401_withoutCredentials() throws Exception {
        // given

        // when / then
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void validate_returns200_whenCredentialsValid() throws Exception {
        // given
        when(userService.validateCredentials(any())).thenReturn(sampleResponse());
        CredentialsRequest request = new CredentialsRequest("john@example.com", "secret123");

        // when / then
        mockMvc.perform(post("/api/v1/users/validate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("john@example.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void validate_returns401_whenCredentialsInvalid() throws Exception {
        // given
        when(userService.validateCredentials(any()))
                .thenThrow(new InvalidCredentialsException("john@example.com"));
        CredentialsRequest request = new CredentialsRequest("john@example.com", "wrong");

        // when / then
        mockMvc.perform(post("/api/v1/users/validate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void search_returns200_whenUserExists() throws Exception {
        // given
        when(userService.getUserByEmail("john@example.com")).thenReturn(sampleResponse());

        // when / then
        mockMvc.perform(get("/api/v1/users/search").param("email", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("john@example.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void search_returns404_whenUserMissing() throws Exception {
        // given
        when(userService.getUserByEmail("missing@example.com"))
                .thenThrow(new UserNotFoundException("missing@example.com"));

        // when / then
        mockMvc.perform(get("/api/v1/users/search").param("email", "missing@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND"));
    }
}
