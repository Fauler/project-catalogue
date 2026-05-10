package com.project.catalogue.project.infrastructure;

import com.project.catalogue.project.domain.exception.ProjectUserNotFoundException;
import com.project.catalogue.project.domain.repository.UserValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Base64;

@Component
public class UserServiceClient implements UserValidator {

    @Value("${clients.user-service.base-url}")
    private String userServiceBaseUrl;

    @Value("${clients.user-service.username}")
    private String username;

    @Value("${clients.user-service.password}")
    private String password;

    @Override
    public void validateUserExists(Long userId) {
        String credentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        try {
            RestClient.create(userServiceBaseUrl)
                    .get()
                    .uri("/api/v1/users/{id}", userId)
                    .header("Authorization", "Basic " + credentials)
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
