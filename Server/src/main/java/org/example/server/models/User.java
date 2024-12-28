package org.example.server.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    private String role;

    @ManyToMany(mappedBy = "assignedUsers")
    @JsonBackReference  // Prevents recursive serialization
    private Set<Task> tasks = new HashSet<>();

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Set<Task> getTasks() { return tasks; }
    public void setTasks(Set<Task> tasks) { this.tasks = tasks; }
}
