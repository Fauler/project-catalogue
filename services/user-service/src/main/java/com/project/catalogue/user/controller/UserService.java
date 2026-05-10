package com.project.catalogue.user.controller;

import com.project.catalogue.user.boundary.UserRequest;
import com.project.catalogue.user.boundary.UserResponse;
import com.project.catalogue.user.domain.exception.UserAlreadyExistsException;
import com.project.catalogue.user.domain.exception.UserNotFoundException;
import com.project.catalogue.user.domain.model.User;
import com.project.catalogue.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    /**
     * Creates a user and validate unique email.
     */
    public UserResponse createUser(UserRequest request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        User user = new User(
                null,
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                null,
                null
        );
        User saved = repository.save(user);
        return toResponse(saved);
    }

    public UserResponse getUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return toResponse(user);
    }

    public UserResponse updateUser(Long id, UserRequest request){
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        return toResponse(repository.save(user));
    }

    public String deleteUser(Long id){
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        repository.deleteById(id);
        return "User %s %s (%s) has been successfully deleted.".formatted(user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public Page<UserResponse> listUsers(int page, int size) {
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
