package com.itmd510.issuetrackingapplication.config;

import java.util.Objects;

public class SessionManager {

    private static SessionManager instance;
    private String currentUser;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void login(String username) {
        Objects.requireNonNull(username, "Username cannot be null");
        if (currentUser == null) {
            currentUser = username;
        } else {
            throw new IllegalStateException("A user is already logged in");
        }
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public String getLoggedInUsername() {
        return currentUser;
    }
}
