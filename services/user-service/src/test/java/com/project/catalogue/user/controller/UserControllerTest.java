package com.project.catalogue.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.catalogue.user.boundary.UserRequest;
import com.project.catalogue.user.boundary.UserResponse;
import com.project.catalogue.user.domain.exception.UserNotFoundException;
import com.project.catalogue.user.infrastructure.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

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

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private UserResponse sampleResponse() {
        return UserResponse.builder()
                .id(1L)
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser
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
    @WithMockUser
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
    @WithMockUser
    void returns200_withUserData() throws Exception {
        // given
        when(userService.getUser(1L)).thenReturn(sampleResponse());

        // when / then
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser
    void returns404_whenUserDoesNotExist() throws Exception {
        // given
        when(userService.getUser(99L)).thenThrow(new UserNotFoundException(99L));

        // when / then
        mockMvc.perform(get("/api/v1/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND"));
    }

    @Test
    @WithMockUser
    void returns200_onSuccessfulDelete() throws Exception {
        // given
        when(userService.deleteUser(1L)).thenReturn("User John Doe (john@example.com) has been successfully deleted.");

        // when / then
        mockMvc.perform(delete("/api/v1/users/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("User John Doe (john@example.com) has been successfully deleted."));
    }

    @Test
    void returns401_withoutCredentials() throws Exception {
        // given

        // when / then
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isUnauthorized());
    }
}
