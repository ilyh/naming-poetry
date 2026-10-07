package com.example.naming.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AdminAuthService {

    private static final Duration SESSION_TTL = Duration.ofHours(8);

    @Value("${admin.password:}")
    private String configuredPassword;

    private final SecureRandom secureRandom = new SecureRandom();
    private final ConcurrentMap<String, Instant> sessions = new ConcurrentHashMap<>();

    public boolean isPasswordConfigured() {
        return configuredPassword != null && !configuredPassword.isBlank();
    }

    public boolean matchesPassword(String password) {
        if (!isPasswordConfigured() || password == null) {
            return false;
        }
        return MessageDigest.isEqual(
            configuredPassword.getBytes(StandardCharsets.UTF_8),
            password.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String createSession() {
        Instant now = Instant.now();
        sessions.entrySet().removeIf(entry -> !entry.getValue().isAfter(now));

        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        sessions.put(token, now.plus(SESSION_TTL));
        return token;
    }

    public boolean hasValidSession(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        Instant expiresAt = sessions.get(token);
        if (expiresAt == null) {
            return false;
        }
        if (!expiresAt.isAfter(Instant.now())) {
            sessions.remove(token, expiresAt);
            return false;
        }
        return true;
    }

    public void revokeSession(String token) {
        if (token != null) {
            sessions.remove(token);
        }
    }

    public long getSessionTtlSeconds() {
        return SESSION_TTL.toSeconds();
    }
}
