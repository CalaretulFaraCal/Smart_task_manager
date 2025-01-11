package org.example.server.services;

import org.example.server.dto.SubtaskCreateRequest;
import org.example.server.models.Subtask;
import org.example.server.models.Task;
import org.example.server.repositories.SubtaskRepository;
import org.example.server.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubtaskService {

    @Autowired
    private SubtaskRepository subtaskRepository;

    @Autowired
    private TaskRepository taskRepository;

    // Create a new subtask
    public Subtask createSubtask(Long parentTaskId, SubtaskCreateRequest request) {
        Task parentTask = taskRepository.findById(parentTaskId)
                .orElseThrow(() -> new IllegalArgumentException("Parent task not found"));

        Subtask subtask = new Subtask();
        subtask.setTitle(request.getTitle());
        subtask.setCompleted(request.isCompleted());
        subtask.setParentTask(parentTask);

        return subtaskRepository.save(subtask);
    }

    // Fetch subtasks by parent task ID
    public List<Subtask> getSubtasksByParentTaskId(Long parentTaskId) {
        return subtaskRepository.findByParentTaskId(parentTaskId);
    }

    public Subtask getSubtaskById(Long id) {
        return subtaskRepository.findById(id).orElse(null);
    }

    public Subtask saveSubtask(Subtask subtask) {
        return subtaskRepository.save(subtask);
    }
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}


