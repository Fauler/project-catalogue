package com.project.catalogue.user.controller;

import com.project.catalogue.user.boundary.UserRequest;
import com.project.catalogue.user.boundary.UserResponse;
import com.project.catalogue.user.boundary.UserUpdateRequest;
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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a user and validates unique email.
     */
    @Transactional
    public UserResponse createUser(UserRequest request) {
        log.info("Creating user with email {}", request.email());
        if (repository.findByEmail(request.email()).isPresent()) {
            log.warn("Duplicate email attempt: {}", request.email());
            throw new UserAlreadyExistsException(request.email());
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

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

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        log.info("Updating user {}", id);
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        repository.findByEmail(request.email())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    log.warn("Email conflict on update: {} already taken by user {}", request.email(), existing.getId());
                    throw new UserAlreadyExistsException(request.email());
                });

        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        return toResponse(repository.save(user));
    }

    @Transactional
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
        log.debug("Listing users - page {}, size {}", page, size);
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
