package com.project.catalogue.auth.infrastructure;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String TEST_SECRET =
            "bG9jYWwtZGV2LXNlY3JldC1rZXktZm9yLXByb2plY3QtY2F0YWxvZ3VlLXBvYw==";
    private static final long EXPIRATION_MS = 3600000L;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(new JwtProperties(TEST_SECRET, EXPIRATION_MS));
    }

    @Test
    void generateToken_containsSubjectAndRole() {
        String token = jwtService.generateToken("client-admin-01", "ADMIN");

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET));
        Claims claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();

        assertThat(claims.getSubject()).isEqualTo("client-admin-01");
        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    void generateToken_userRole() {
        String token = jwtService.generateToken("client-user-01", "USER");

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET));
        Claims claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();

        assertThat(claims.get("role", String.class)).isEqualTo("USER");
    }

    @Test
    void getExpirationMs_returnsConfiguredValue() {
        assertThat(jwtService.getExpirationMs()).isEqualTo(EXPIRATION_MS);
    }
}

