package com.project.catalogue.project.infrastructure;

import com.project.catalogue.project.domain.exception.ProjectUserNotFoundException;
import com.project.catalogue.project.domain.repository.UserValidator;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class UserServiceClient implements UserValidator {

    @Value("${clients.user-service.base-url}")
    private String userServiceBaseUrl;

    private RestClient restClient;

    @PostConstruct
    void init() {
        this.restClient = RestClient.builder()
                .baseUrl(userServiceBaseUrl)
                .build();
    }

    @Override
    public void validateUserExists(Long userId) {
        log.debug("Validating userId {} via user-service", userId);
        String bearerToken = extractBearerToken();
        try {
            restClient.get()
                    .uri("/api/v1/users/{id}", userId)
                    .header("Authorization", bearerToken)
                    .retrieve()
                    .toBodilessEntity();
            log.debug("userId {} confirmed", userId);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                log.warn("user-service returned 404 for userId {}", userId);
                throw new ProjectUserNotFoundException(userId);
            }
            log.error("user-service call failed for userId {} - status {}", userId, ex.getStatusCode());
            throw ex;
        }
    }

    private String extractBearerToken() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new IllegalStateException("No active HTTP request context");
        }
        HttpServletRequest request = attrs.getRequest();
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalStateException("No Bearer token found in current request");
        }
        return authHeader;
    }
}
