package org.example.server.services;

import org.example.server.models.Task;
import org.example.server.models.User;
import org.example.server.dto.TaskCreateRequest;
import org.example.server.repositories.TaskRepository;
import org.example.server.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.List;
import java.util.HashSet;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    // Single constructor for dependency injection
    public TaskService(TaskRepository taskRepository, UserService userService, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // Create a task and assign users
    public Task createTask(TaskCreateRequest taskCreateRequest) throws IllegalArgumentException {
        // Create the task
        Task task = new Task();
        task.setTitle(taskCreateRequest.getTitle());
        task.setDescription(taskCreateRequest.getDescription());
        task.setCategory(taskCreateRequest.getCategory());
        task.setPriority(taskCreateRequest.getPriority());
        task.setDeadline(taskCreateRequest.getDeadline());
        task.setCompleted(taskCreateRequest.isCompleted());

        System.out.println("Checkpoint after new Task reached!");

        // Assign users to the task
        Set<User> assignedUsers = new HashSet<>();
        for (Long userId : taskCreateRequest.getUserIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " does not exist"));
            assignedUsers.add(user);
        }
        task.setAssignedUsers(assignedUsers);

        System.out.println("Checkpoint after assigning users reached!");

        // Save and return the task
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

    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findTasksByUserId(userId);
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
        return taskRepository.save(existingTask);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }


}
