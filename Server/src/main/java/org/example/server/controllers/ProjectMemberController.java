package org.example.server.controllers;

import org.example.server.models.Project;
import org.example.server.services.ProjectMemberService;
import org.example.server.services.ProjectService;
import org.example.server.dto.UserAssignmentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project_members")
public class ProjectMemberController {

    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;

    public ProjectMemberController(ProjectService projectService, ProjectMemberService projectMemberService) {
        this.projectService = projectService;
        this.projectMemberService = projectMemberService;
    }

    @PostMapping("/{projectId}/assign-users")
    public ResponseEntity<Project> assignUsersToProject(@PathVariable Long projectId, @RequestBody UserAssignmentRequest request) {
        Project project = projectMemberService.assignUsersToProject(projectId, request.getUserIds(), request.getRole());
        return ResponseEntity.ok(project);
    }
}
