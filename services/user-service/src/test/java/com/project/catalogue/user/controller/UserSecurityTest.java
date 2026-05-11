package com.project.catalogue.user.controller;

import com.project.catalogue.user.boundary.UserResponse;
import com.project.catalogue.user.infrastructure.JwtService;
import com.project.catalogue.user.infrastructure.SecurityConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "jwt.secret=bG9jYWwtZGV2LXNlY3JldC1rZXktZm9yLXByb2plY3QtY2F0YWxvZ3VlLXBvYw=="
})
class UserSecurityTest {

    private static final String TEST_SECRET =
            "bG9jYWwtZGV2LXNlY3JldC1rZXktZm9yLXByb2plY3QtY2F0YWxvZ3VlLXBvYw==";

    private static final String TAMPERED_BEARER =
            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWNrZXJAZXZpbC5jb20ifQ.BADSIG";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @BeforeEach
    void setupJwtDelegate() {
        JwtService real = new JwtService();
        ReflectionTestUtils.setField(real, "secret", TEST_SECRET);
        ReflectionTestUtils.invokeMethod(real, "init");
        lenient().when(jwtService.validateToken(any()))
                .thenAnswer(inv -> real.validateToken(inv.getArgument(0)));
    }

    private String expiredBearer() {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET));
        return "Bearer " + Jwts.builder()
                .subject("client-user-01")
                .claim("role", "USER")
                .issuedAt(new Date(System.currentTimeMillis() - 7_200_000))
                .expiration(new Date(System.currentTimeMillis() - 3_600_000))
                .signWith(key)
                .compact();
    }

    private UserResponse sampleUser() {
        return UserResponse.builder()
                .id(1L).email("john@example.com")
                .firstName("John").lastName("Doe")
                .createdAt(LocalDateTime.now())
                .build();
    }

    // -- 401: no token --

    @Test
    void getUser_noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createUser_noToken_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/users").with(csrf())
                        .contentType("application/json")
                        .content("{\"email\":\"a@b.com\",\"firstName\":\"A\",\"lastName\":\"B\",\"password\":\"secret12\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUser_noToken_returns401() throws Exception {
        mockMvc.perform(put("/api/v1/users/1").with(csrf())
                        .contentType("application/json")
                        .content("{\"email\":\"a@b.com\",\"firstName\":\"A\",\"lastName\":\"B\",\"password\":\"secret12\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_noToken_returns401() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    // -- 401: bad token --

    @Test
    void getUser_tamperedToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/1")
                        .header("Authorization", TAMPERED_BEARER))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUser_expiredToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/1")
                        .header("Authorization", expiredBearer()))
                .andExpect(status().isUnauthorized());
    }

    // -- 200: authenticated with role --

    @Test
    @WithMockUser(roles = "USER")
    void getUser_roleUser_returns200() throws Exception {
        when(userService.getUser(1L)).thenReturn(sampleUser());
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk());
    }

    // -- 403: USER role cannot delete --

    @Test
    @WithMockUser(roles = "USER")
    void deleteUser_roleUser_returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_roleAdmin_returns200() throws Exception {
        when(userService.deleteUser(1L)).thenReturn("User deleted");
        mockMvc.perform(delete("/api/v1/users/1").with(csrf()))
                .andExpect(status().isOk());
    }
}
