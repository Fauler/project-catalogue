package com.project.catalogue.user.controller;

import com.project.catalogue.user.boundary.UserRequest;
import com.project.catalogue.user.boundary.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> listUsers(
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, userService.listUsers(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, userService.getUser(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable @Positive Long id, @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, userService.updateUser(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable @Positive Long id) {
        String message = userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, message));
    }
}
