package org.example.server.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.example.server.models.Phase;
import org.example.server.models.Project;
import org.example.server.models.Task;
import org.example.server.repositories.ProjectRepository;
import org.example.server.repositories.TaskRepository;
import org.example.server.services.ProjectService;
import org.example.server.services.SubtaskService;
import org.example.server.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public ProjectController(ProjectService projectService, TaskService taskService, ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Project> createProject(@PathVariable Long userId, @RequestBody Project project) {
        System.out.println("Step 1: Entered createProject method in ProjectController");
        System.out.println("Received project: " + project);

        if (project.getPhase() == null) {
            System.out.println("Step 2: Phase is null, setting it to NOT_STARTED");
            project.setPhase(Phase.NOT_STARTED);
        } else {
            System.out.println("Step 2: Phase is already set to: " + project.getPhase());
        }

        // Call the service to create the project
        Project createdProject = projectService.createProject(project, userId);
        System.out.println("Step 3: Project created successfully: " + createdProject);

        return ResponseEntity.ok(createdProject);
    }

    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<String> assignTaskToProject(@PathVariable Long projectId, @RequestBody Task task) {
        try {
            projectService.assignTaskToProject(projectId, task);
            return ResponseEntity.ok("Task assigned to project successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable String id) {
        Project project = projectService.getProjectById(Long.parseLong(id));
        return ResponseEntity.ok(project);
    }

    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<Task>> getTasksForProject(@PathVariable Long projectId) {
        // Fetch tasks directly using the project ID
        List<Task> tasks = taskRepository.findTasksByProjectId(projectId);

        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no tasks found
        }

        return ResponseEntity.ok(tasks); // Return tasks as a list
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable String id, @RequestBody Project project) {
        Project updatedProject = projectService.updateProject(Long.parseLong(id), project);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id, @RequestParam(value = "deleteTasks", required = false, defaultValue = "false") boolean deleteTasks) {
        projectService.deleteProject(id, deleteTasks);
        return ResponseEntity.ok("Project deleted successfully" + (deleteTasks ? " along with tasks." : "."));
    }
}
