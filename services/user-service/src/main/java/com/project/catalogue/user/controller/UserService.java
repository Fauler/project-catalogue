package com.project.catalogue.user.controller;

import com.project.catalogue.user.boundary.UserRequest;
import com.project.catalogue.user.boundary.UserResponse;
import com.project.catalogue.user.domain.exception.UserAlreadyExistsException;
import com.project.catalogue.user.domain.exception.UserNotFoundException;
import com.project.catalogue.user.domain.model.User;
import com.project.catalogue.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a user and validate unique email.
     */
    public UserResponse createUser(UserRequest request) {
        log.info("Creating user with email {}", request.getEmail());
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Duplicate email attempt: {}", request.getEmail());
            throw new UserAlreadyExistsException(request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User saved = repository.save(user);
        log.debug("User {} created with id {}", saved.getEmail(), saved.getId());
        return toResponse(saved);
    }

    public UserResponse getUser(Long id) {
        log.debug("Fetching user with id {}", id);
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return toResponse(user);
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        log.info("Updating user {}", id);
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        return toResponse(repository.save(user));
    }

    public String deleteUser(Long id) {
        log.info("Deleting user {}", id);
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        repository.deleteById(id);
        log.debug("User {} ({}) deleted", id, user.getEmail());
        return "User %s %s (%s) has been successfully deleted.".formatted(
                user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public Page<UserResponse> listUsers(int page, int size) {
        log.debug("Listing users — page {}, size {}", page, size);
        return repository.findAll(PageRequest.of(page, size)).map(this::toResponse);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
