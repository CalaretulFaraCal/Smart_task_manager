package org.example.client.models;

public class Task {
    private Long id;
    private String title;
    private String description;
    private String category;
    private boolean completed;
    private String priority;
    private String deadline;
    private Long parentTaskId;

    public Task() {
    }

    public Task(String title, String deadline) {
        this.title = title;
        this.deadline = deadline;
    }

    public Task(String title, String category, String priority, String deadline, boolean completed) {
        this.title = title;
        this.category = category;
        this.priority = priority;
        this.deadline = deadline;
        this.completed = completed;
    }

    @Override
    public String toString() {
        return title + " - " + deadline;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDeadline() {
        return deadline;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public Long getParentTaskId() {
        return parentTaskId;
    }
    public void setParentTaskId(Long parentTaskId) {
        this.parentTaskId = parentTaskId;
    }
}