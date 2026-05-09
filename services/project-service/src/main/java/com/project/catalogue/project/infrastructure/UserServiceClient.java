package com.project.catalogue.project.infrastructure;

import com.project.catalogue.project.domain.ProjectUserNotFoundException;
import com.project.catalogue.project.domain.UserValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class UserServiceClient implements UserValidator {

    @Value("${clients.user-service.base-url:http://localhost:8080}")
    private String userServiceBaseUrl;

    @Override
    public void validateUserExists(Long userId) {
        try {
            RestClient.create(userServiceBaseUrl)
                    .get()
                    .uri("/api/v1/users/{id}", userId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new ProjectUserNotFoundException(userId);
            }
            throw ex;
        }
    }
}

