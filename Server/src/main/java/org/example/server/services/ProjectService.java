package org.example.server.services;

import org.example.server.models.Phase;
import org.example.server.models.Project;
import org.example.server.models.Task;
import org.example.server.models.User;
import org.example.server.repositories.ProjectRepository;
import org.example.server.repositories.TaskRepository;
import org.example.server.repositories.UserRepository;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public Project createProject(Project project, Long userId) {
        System.out.println("Step 4: Entered createProject method in ProjectService");
        System.out.println("Initial project details: " + project);

        // Fetch the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    System.out.println("Step 5: User not found with ID: " + userId);
                    return new IllegalArgumentException("User with ID " + userId + " does not exist");
                });
        System.out.println("Step 6: User fetched successfully: " + user);

        // Set the user and ensure the phase is NOT_STARTED
        project.setUser(user);
        if (project.getPhase() == null) {
            System.out.println("Step 7: Phase is null, setting it to NOT_STARTED");
            project.setPhase(Phase.NOT_STARTED);
        } else {
            System.out.println("Step 7: Phase is already set to: " + project.getPhase());
        }

        // Save the project
        Project savedProject = projectRepository.save(project);
        System.out.println("Step 8: Project saved successfully: " + savedProject);

        return savedProject;
    }

    public void assignTaskToProject(Long projectId, Task task) {
        Project project = getProjectById(projectId);
        task.setProject(project);
        taskRepository.save(task); // Save updated task
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project updateProject(Long id, Project projectDetails) {
        Project project = getProjectById(id);

        project.setTitle(projectDetails.getTitle());
        project.setPhase(projectDetails.getPhase());

        return projectRepository.save(project);
    }

    public void deleteProject(Long id, boolean deleteTasks) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
        System.out.println("Found project with ID: " + id);


        if (deleteTasks) {
            // Delete all tasks associated with the project
            List<Task> tasks = taskRepository.findAllByProject(project);
            taskRepository.deleteAll(tasks);
        } else {
            // Disassociate tasks from the project
            List<Task> tasks = taskRepository.findAllByProject(project);
            tasks.forEach(task -> task.setProject(null));
            taskRepository.saveAll(tasks);
        }

        // Finally, delete the project
        projectRepository.delete(project);
    }

    public void removeTaskFromProject(Long projectId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));
        if (task.getProject().getId().equals(projectId)) {
            task.setProject(null);
            taskRepository.save(task); // Save updated task
        }
    }
}
