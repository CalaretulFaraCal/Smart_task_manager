package org.example.server.dto;

import java.util.HashSet;
import java.util.Set;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.server.controllers.TaskController;

public class TaskCreateRequest {
    @NotNull
    private String title;
    private String description;
    private String category;
    private String priority;
    private String deadline;
    private boolean completed;

    @NotEmpty // Ensure the userIds field is not empty
    private Set<Long> userIds = new HashSet<>(); // Add this for user assignment

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public Set<Long> getUserIds() { return userIds; }
    public void setUserIds(Set<Long> userIds) { this.userIds = userIds != null ? userIds : new HashSet<>(); } // Avoid null values}
}
