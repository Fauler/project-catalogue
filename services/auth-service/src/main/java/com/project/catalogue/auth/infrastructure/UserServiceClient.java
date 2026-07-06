package com.project.catalogue.auth.infrastructure;

import com.project.catalogue.auth.domain.exception.InvalidUserCredentialsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Slf4j
@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(RestClient.Builder restClientBuilder, UserServiceClientProperties properties) {
        var settings = HttpClientSettings.defaults()
                .withConnectTimeout(properties.connectTimeout())
                .withReadTimeout(properties.readTimeout());
        var requestFactory = ClientHttpRequestFactoryBuilder.detect().build(settings);

        this.restClient = restClientBuilder
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    public void validateCredentials(String email, String password) {
        log.debug("Validating credentials for {} via user-service", email);
        try {
            restClient.post()
                    .uri("/api/v1/users/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("email", email, "password", password))
                    .retrieve()
                    .toBodilessEntity();
            log.debug("Credentials confirmed for {}", email);
        } catch (RestClientResponseException ex) {
            int status = ex.getStatusCode().value();
            if (status == 401 || status == 404) {
                log.warn("user-service rejected credentials for {} - status {}", email, status);
                throw new InvalidUserCredentialsException(email);
            }
            log.error("user-service call failed for {} - status {}", email, ex.getStatusCode());
            throw ex;
        }
    }
}
