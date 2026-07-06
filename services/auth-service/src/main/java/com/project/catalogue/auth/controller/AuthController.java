package com.project.catalogue.auth.controller;

import com.project.catalogue.auth.boundary.ApiResult;
import com.project.catalogue.auth.boundary.TokenRequest;
import com.project.catalogue.auth.boundary.TokenResponse;
import com.project.catalogue.auth.domain.exception.UnauthorizedClientException;
import com.project.catalogue.auth.infrastructure.AuthProperties;
import com.project.catalogue.auth.infrastructure.JwtService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final AuthProperties authProperties;
    private final Counter tokensIssued;

    public AuthController(JwtService jwtService, AuthProperties authProperties, MeterRegistry registry) {
        this.jwtService = jwtService;
        this.authProperties = authProperties;
        this.tokensIssued = Counter.builder("tokens.issued").description("Tokens issued").register(registry);
    }

    @Operation(summary = "Issue JWT token for a registered client")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token issued"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unknown clientId")
    })
    @PostMapping("/token")
    public ResponseEntity<ApiResult<TokenResponse>> token(@Valid @RequestBody TokenRequest request) {
        String clientId = request.clientId();

        if (!authProperties.allowedClientIds().contains(clientId)) {
            log.warn("Rejected unknown clientId {}", clientId);
            throw new UnauthorizedClientException(clientId);
        }

        String role = authProperties.adminClientIds().contains(clientId) ? "ADMIN" : "USER";
        log.debug("Issuing token for clientId {} with role {}", clientId, role);

        String token = jwtService.generateToken(clientId, role);
        tokensIssued.increment();
        long expiresIn = jwtService.getExpirationMs() / 1000;
        return ResponseEntity.ok(ApiResult.success(HttpStatus.OK, new TokenResponse(token, expiresIn)));
    }
}
