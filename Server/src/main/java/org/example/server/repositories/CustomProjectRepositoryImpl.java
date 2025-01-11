package org.example.server.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.server.models.Project;
import org.springframework.stereotype.Repository;

@Repository
public class CustomProjectRepositoryImpl implements Project.CustomProjectRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Project saveWithDebug(Project project) {
        System.out.println("DEBUG: Saving project with EntityManager - " + project);
        Project savedProject = entityManager.merge(project); // Save or update the project
        System.out.println("DEBUG: Saved project details - " + savedProject);
        return savedProject;
    }
}
