package com.project.catalogue.auth.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties(prefix = "auth")
public record AuthProperties(
        Set<String> allowedClientIds,
        Set<String> adminClientIds
) {}

