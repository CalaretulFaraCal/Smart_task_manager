package org.example.server.models;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class Subtask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private boolean isVisibleToAllUsers;  // Flag for visibility (should be editable when creating the subtask)
    private boolean completed;
    private long timeSpent;

    private LocalDateTime deadline;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task parentTask;

    @ManyToMany
    @JoinTable(
            name = "subtask_users",
            joinColumns = @JoinColumn(name = "subtask_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignedUsers;

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

    public boolean isVisibleToAllUsers() {
        return isVisibleToAllUsers;
    }
    public void setVisibleToAllUsers(boolean visibleToAllUsers) {
        isVisibleToAllUsers = visibleToAllUsers;
    }

    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getTimeSpent() {
        return timeSpent;
    }
    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Task getParentTask() {
        return parentTask;
    }
    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }

    public Set<User> getAssignedUsers() {
        return assignedUsers;
    }
    public void setAssignedUsers(Set<User> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }
}
