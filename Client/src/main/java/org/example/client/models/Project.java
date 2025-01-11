package org.example.client.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
    private Long id;
    private String title;
    private String description;
    private String phase; // Add this field if needed

    // Constructors
    public Project() {}

    public Project(String title) {
        this.title = title;
    }

    public Project(Long id, String title, String description, String phase) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.phase = phase;
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

    public void setPhase(String phase) {
        this.phase = phase;
    }
    public String getPhase() {
        return phase;
    }

    @Override
    public String toString() {
        return title; // For display in ComboBox
    }
}
