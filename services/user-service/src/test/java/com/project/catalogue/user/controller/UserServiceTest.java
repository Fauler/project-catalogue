package com.project.catalogue.user.controller;

import com.project.catalogue.user.boundary.UserRequest;
import com.project.catalogue.user.boundary.UserResponse;
import com.project.catalogue.user.boundary.UserUpdateRequest;
import com.project.catalogue.user.domain.exception.UserAlreadyExistsException;
import com.project.catalogue.user.domain.exception.UserNotFoundException;
import com.project.catalogue.user.domain.model.User;
import com.project.catalogue.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    private User user;
    private UserRequest request;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setPassword("hashed");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setCreatedAt(LocalDateTime.now());

        request = new UserRequest("john@example.com", "John", "Doe", "secret123");
    }

    @Test
    void savesUserWithSuccess() {
        // given
        when(repository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(repository.save(any())).thenReturn(user);

        // when
        UserResponse response = service.createUser(request);

        // then
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getFirstName()).isEqualTo("John");
        verify(passwordEncoder).encode("secret123");
    }

    @Test
    void rejectsCreation_whenEmailAlreadyRegistered() {
        // given
        when(repository.findByEmail(request.email())).thenReturn(Optional.of(user));

        // when / then
        assertThatThrownBy(() -> service.createUser(request))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void returnsUser_whenFound() {
        // given
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        // when
        UserResponse response = service.getUser(1L);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void throwsNotFound_whenUserDoesNotExist() {
        // given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> service.getUser(99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updatesEmailAndName() {
        // given
        UserUpdateRequest updateRequest = new UserUpdateRequest("new@example.com", "Jane", "Doe");
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        UserResponse response = service.updateUser(1L, updateRequest);

        // then
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void updateUser_emailConflict_throws() {
        // given
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("taken@example.com");

        UserUpdateRequest updateRequest = new UserUpdateRequest("taken@example.com", "Jane", "Doe");
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.findByEmail("taken@example.com")).thenReturn(Optional.of(otherUser));

        // when / then
        assertThatThrownBy(() -> service.updateUser(1L, updateRequest))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void updateUser_sameEmailSameUser_succeeds() {
        // given
        UserUpdateRequest updateRequest = new UserUpdateRequest("john@example.com", "Jane", "Doe");
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        UserResponse response = service.updateUser(1L, updateRequest);

        // then
        assertThat(response.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void updateUser_notFound_throws() {
        // given
        when(repository.findById(99L)).thenReturn(Optional.empty());
        UserUpdateRequest updateRequest = new UserUpdateRequest("new@example.com", "Jane", "Doe");

        // when / then
        assertThatThrownBy(() -> service.updateUser(99L, updateRequest))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void deletesUserSuccess() {
        // given
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        // when
        String message = service.deleteUser(1L);

        // then
        assertThat(message).contains("john@example.com");
        verify(repository).deleteById(1L);
    }

    @Test
    void deleteUser_notFound_throws() {
        // given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> service.deleteUser(99L))
                .isInstanceOf(UserNotFoundException.class);
    }
}
