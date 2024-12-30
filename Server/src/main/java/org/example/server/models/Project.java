package org.example.server.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Phase phase;

    private LocalDateTime created;
    private LocalDateTime updated;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        created = now;
        updated = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updated = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> taskList;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ProjectMember> projectMembers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public Project(Long userId, String title, String description, Phase phase) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.phase = phase;
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }
}
