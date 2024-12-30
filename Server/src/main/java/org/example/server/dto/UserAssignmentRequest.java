package org.example.server.dto;

import java.util.List;

public class UserAssignmentRequest {
    private List<Long> userIds;
    private String role;

    // Getters and Setters
    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
