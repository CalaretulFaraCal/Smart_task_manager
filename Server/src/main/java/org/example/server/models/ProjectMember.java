package org.example.server.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_members")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonBackReference
    private Project project;

    @Column(name = "user_id")
    private String userId;

    private String role;

    public ProjectMember(Project project, String userId, String role) {
        this.project = project;
        this.userId = userId;
        this.role = role;
    }
}
