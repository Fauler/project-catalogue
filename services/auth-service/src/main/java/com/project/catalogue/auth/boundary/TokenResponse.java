package com.project.catalogue.auth.boundary;

public record TokenResponse(String token, long expiresIn) {}
