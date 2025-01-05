package org.example.client.services;

import org.example.client.models.User;

public class UserService {

    private final BackendService backendService = new BackendService();

    public boolean login(String email, String password) {
        return backendService.loginUser(email, password);
    }

    public boolean register(String username, String email, String password) {
        return backendService.registerUser(username, email, password);
    }

    public Long getCurrentUserId() {
        return backendService.getSavedUserId();
    }

    // Fetch user details by ID
    public User getUserById(Long userId) throws Exception {
        return backendService.getUserById(userId);
    }

    // Update user details
    public boolean updateUser(Long userId, String username, String email, String password) {
        return backendService.updateUser(userId, username, email, password);
    }
}

