package com.otptgbot.SecurityUtilities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class TokenHandler {

    private final SecretKey secretSigningKey;
    private final long tokenExpirationMs;

    public TokenHandler(
            @Value("${jwt.secret}") String secretBase64,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.secretSigningKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretBase64));
        this.tokenExpirationMs = expirationMs;
    }

    public String generateToken(String userLogin, String userRole) {
        return Jwts.builder()
                .subject(userLogin)
                .claim("role", userRole)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpirationMs))
                .signWith(secretSigningKey)
                .compact();
    }

    public Claims parseToken(String jwtToken) {
        return Jwts.parser()
                .verifyWith(secretSigningKey)
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    public String extractLogin(String jwtToken) {
        return parseToken(jwtToken).getSubject();
    }

    public boolean isValid(String jwtToken) {
        try {
            parseToken(jwtToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}