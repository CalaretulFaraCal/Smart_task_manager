package org.example.client.services;

import org.example.client.services.BackendService;

public class UserService {

    private final BackendService backendService = new BackendService();

    public boolean login(String email, String password) {
        return backendService.loginUser(email, password);
    }

    public boolean register(String username, String email, String password) {
        return backendService.registerUser(username, email, password);
    }
}
