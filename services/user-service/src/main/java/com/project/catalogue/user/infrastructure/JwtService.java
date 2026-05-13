package com.project.catalogue.user.infrastructure;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Slf4j
@Service
public class JwtService {

    private final SecretKey signingKey;

    public JwtService(JwtProperties properties) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
        log.debug("JWT signing key initialized");
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException ex) {
            log.warn("Invalid JWT token: {}", ex.getMessage());
            throw ex;
        }
    }
}
