package org.example.server.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String category;
    private String priority;
    private String deadline;
    private boolean completed;
    private boolean notificationSent = false; // New field

    @Column(name = "notify_before_hours")
    private Integer notifyBeforeHours;

    @ManyToMany
    @JoinTable(
            name = "task_users",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignedUsers = new HashSet<>();

    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Handle forward reference for subtasks
    private List<Task> subtasks = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "parent_task_id")
    @JsonBackReference // Handle backward reference for parent task
    private Task parentTask;

    private boolean timerRunning;
    private long timerStart;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Set<User> getAssignedUsers() { return assignedUsers; }
    public void setAssignedUsers(Set<User> assignedUsers) { this.assignedUsers = assignedUsers; }

    public List<Task> getSubtasks() { return subtasks; }
    public void setSubtasks(List<Task> subtasks) { this.subtasks = subtasks; }

    public Task getParentTask() { return parentTask; }
    public void setParentTask(Task parentTask) { this.parentTask = parentTask; }

    public boolean isTimerRunning() { return timerRunning; }
    public void setTimerRunning(boolean timerRunning) { this.timerRunning = timerRunning; }

    public long getTimerStart() { return timerStart; }
    public void setTimerStart(long timerStart) { this.timerStart = timerStart; }

    public boolean isNotificationSent() {
        return notificationSent;
    }
    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    public Integer getNotifyBeforeHours() {
        return notifyBeforeHours;
    }
    public void setNotifyBeforeHours(Integer notifyBeforeHours) {
        this.notifyBeforeHours = notifyBeforeHours;
    }
}
