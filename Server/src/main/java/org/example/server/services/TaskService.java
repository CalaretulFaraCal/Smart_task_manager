package org.example.server.services;

import org.example.server.models.Task;
import org.example.server.models.User;
import org.example.server.dto.TaskCreateRequest;
import org.example.server.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.List;
import java.util.HashSet;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    // Create a task and assign users
    public Task createTask(TaskCreateRequest taskCreateRequest) {
        // Initialize the task entity
        Task task = new Task();
        task.setTitle(taskCreateRequest.getTitle());
        task.setDescription(taskCreateRequest.getDescription());
        task.setCategory(taskCreateRequest.getCategory());
        task.setPriority(taskCreateRequest.getPriority());
        task.setDeadline(taskCreateRequest.getDeadline());
        task.setCompleted(taskCreateRequest.isCompleted());
        task.setTimeSpent(taskCreateRequest.getTimeSpent());

        // Check if userIds is null or empty
        if (taskCreateRequest.getUserIds() == null || taskCreateRequest.getUserIds().isEmpty()) {
            throw new IllegalArgumentException("User IDs must not be null or empty");
        }

        // Assign users
        Set<User> assignedUsers = new HashSet<>();
        for (Long userId : taskCreateRequest.getUserIds()) {
            // Ensure userId is not null and fetch the user
            if (userId != null) {
                User user = userService.getUserById(userId);
                if (user != null) { // Check if user exists
                    assignedUsers.add(user);
                } else {
                    throw new IllegalArgumentException("User with ID " + userId + " does not exist");
                }
            } else {
                throw new IllegalArgumentException("User ID cannot be null");
            }
        }
        task.setAssignedUsers(assignedUsers);

        // Save the task and return
        return taskRepository.save(task);
    }


    // Create a subtask under an existing task
    public Task createSubtask(Long parentTaskId, TaskCreateRequest subtaskCreateRequest) {
        Task parentTask = taskRepository.findById(parentTaskId)
                .orElseThrow(() -> new IllegalArgumentException("Parent task not found"));

        Task subtask = new Task();
        subtask.setTitle(subtaskCreateRequest.getTitle());
        subtask.setDescription(subtaskCreateRequest.getDescription());
        subtask.setCategory(subtaskCreateRequest.getCategory());
        subtask.setPriority(subtaskCreateRequest.getPriority());
        subtask.setDeadline(subtaskCreateRequest.getDeadline());
        subtask.setCompleted(subtaskCreateRequest.isCompleted());
        subtask.setTimeSpent(subtaskCreateRequest.getTimeSpent());

        // Set the parent task for the subtask
        subtask.setParentTask(parentTask);

        // Save the subtask and return
        parentTask.getSubtasks().add(subtask);
        taskRepository.save(parentTask);  // Save parent task along with the subtask

        return subtask;
    }

    // Other methods (update, delete, etc.) for task management
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task existingTask = getTaskById(id);
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setCategory(updatedTask.getCategory());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setDeadline(updatedTask.getDeadline());
        existingTask.setCompleted(updatedTask.isCompleted());
        existingTask.setTimeSpent(updatedTask.getTimeSpent());
        return taskRepository.save(existingTask);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
