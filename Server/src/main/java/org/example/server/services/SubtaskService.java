package org.example.server.services;

import org.example.server.dto.SubtaskCreateRequest;
import org.example.server.models.Subtask;
import org.example.server.models.Task;
import org.example.server.models.User;
import org.example.server.repositories.SubtaskRepository;
import org.example.server.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class SubtaskService {

    @Autowired
    private SubtaskRepository subtaskRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;  // Assumed service for fetching users

    // Create a new subtask
    public Subtask createSubtask(Long taskId, SubtaskCreateRequest request) {
        Task parentTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));

        Subtask subtask = new Subtask();
        subtask.setTitle(request.getTitle());
        subtask.setDescription(request.getDescription());
        subtask.setVisibleToAllUsers(request.isVisibleToAllUsers());
        subtask.setParentTask(parentTask);

        return subtaskRepository.save(subtask);
    }

    // Other subtask-related methods like update, delete, etc. can go here
}

