package com.project.catalogue.auth.controller;

import com.project.catalogue.auth.boundary.TokenRequest;
import com.project.catalogue.auth.boundary.TokenResponse;
import com.project.catalogue.auth.domain.exception.UnauthorizedClientException;
import com.project.catalogue.auth.infrastructure.JwtService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final List<String> allowedClientIds;
    private final List<String> adminClientIds;

    public AuthController(JwtService jwtService,
                          @Value("${auth.allowed-client-ids}") List<String> allowedClientIds,
                          @Value("${auth.admin-client-ids}") List<String> adminClientIds) {
        this.jwtService = jwtService;
        this.allowedClientIds = allowedClientIds;
        this.adminClientIds = adminClientIds;
    }

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<TokenResponse>> token(@Valid @RequestBody TokenRequest request) {
        String clientId = request.clientId();

        if (!allowedClientIds.contains(clientId)) {
            log.warn("Rejected unknown clientId {}", clientId);
            throw new UnauthorizedClientException(clientId);
        }

        String role = adminClientIds.contains(clientId) ? "ADMIN" : "USER";
        log.debug("Issuing token for clientId {} with role {}", clientId, role);

        String token = jwtService.generateToken(clientId, role);
        long expiresIn = jwtService.getExpirationMs() / 1000;
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, new TokenResponse(token, expiresIn)));
    }
}
