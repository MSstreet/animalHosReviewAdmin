package com.example.animalhosreviewadmin.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {
    private static final int SESSION_TIMEOUT_MINUTES = 15;
    private final Map<String, LocalDateTime> activeSessions = new ConcurrentHashMap<>();

    public void createSession(String username, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(SESSION_TIMEOUT_MINUTES * 60);
        activeSessions.put(username, LocalDateTime.now());
    }

    public boolean isSessionValid(String username) {
        LocalDateTime lastActivity = activeSessions.get(username);
        if (lastActivity == null) {
            return false;
        }
        return !lastActivity.plusMinutes(SESSION_TIMEOUT_MINUTES).isBefore(LocalDateTime.now());
    }

    public void updateSessionActivity(String username) {
        activeSessions.put(username, LocalDateTime.now());
    }

    public void invalidateSession(String username) {
        activeSessions.remove(username);
    }

    public void cleanupExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        activeSessions.entrySet().removeIf(entry -> 
            entry.getValue().plusMinutes(SESSION_TIMEOUT_MINUTES).isBefore(now));
    }
} 