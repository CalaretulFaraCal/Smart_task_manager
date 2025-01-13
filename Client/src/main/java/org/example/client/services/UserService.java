package org.example.client.services;

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

    public boolean updateUser(Long userId, String username, String email, String password) {
        return backendService.updateUser(userId, username, email, password);
    }
}

