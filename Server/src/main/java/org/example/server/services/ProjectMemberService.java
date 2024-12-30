package org.example.server.services;

import org.example.server.models.Project;
import org.example.server.models.ProjectMember;
import org.example.server.models.User;
import org.example.server.repositories.ProjectMemberRepository;
import org.example.server.repositories.ProjectRepository;
import org.example.server.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectMemberService(ProjectMemberRepository projectMemberRepository,
                                ProjectRepository projectRepository,
                                UserRepository userRepository) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public Project assignUsersToProject(Long projectId, List<Long> userIds, String role) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        List<User> users = userRepository.findAllById(userIds);
        if (users.isEmpty()) {
            throw new IllegalArgumentException("No users found for provided IDs");
        }

        for (User user : users) {
            ProjectMember projectMember = new ProjectMember(project, user.getId().toString(), role);
            projectMemberRepository.save(projectMember);
        }

        return project;
    }
}

