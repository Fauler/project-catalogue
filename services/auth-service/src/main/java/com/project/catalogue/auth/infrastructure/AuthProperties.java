package com.project.catalogue.auth.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "auth")
public record AuthProperties(
        List<String> allowedClientIds,
        List<String> adminClientIds
) {}

