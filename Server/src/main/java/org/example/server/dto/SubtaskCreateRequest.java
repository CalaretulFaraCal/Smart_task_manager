package org.example.server.dto;

import jakarta.validation.constraints.NotNull;

public class SubtaskCreateRequest {

    @NotNull
    private String title;
    private String description;
    private boolean completed;
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

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

}
