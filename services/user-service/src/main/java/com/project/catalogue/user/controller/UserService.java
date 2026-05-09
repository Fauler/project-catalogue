package com.project.catalogue.user.controller;

import com.project.catalogue.user.boundary.UserRequest;
import com.project.catalogue.user.boundary.UserResponse;
import com.project.catalogue.user.domain.User;
import com.project.catalogue.user.domain.UserAlreadyExistsException;
import com.project.catalogue.user.domain.UserNotFoundException;
import com.project.catalogue.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
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

    public void deleteUser(Long id){
        if(!repository.existsById(id)){
            throw new UserNotFoundException(id);
        }
        repository.deleteById(id);
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
