package org.example.server.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class SubtaskCreateRequest {

    @NotNull
    private String title;

    private String description;

    private boolean isVisibleToAllUsers;
    // Getters and Setters

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVisibleToAllUsers() {
        return isVisibleToAllUsers;
    }
    public void setVisibleToAllUsers(boolean visibleToAllUsers) {
        isVisibleToAllUsers = visibleToAllUsers;
    }

}

