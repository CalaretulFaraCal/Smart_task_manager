package org.example.server.services;

import org.example.server.models.Task;
import org.example.server.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TimerService {

    private final TaskRepository taskRepository;

    // To track the start time of a task for timer functionality
    private LocalDateTime startTime;

    public TimerService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Start Timer for the task
    public void startTimer(Long taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            if (!task.isCompleted()) {
                this.startTime = LocalDateTime.now();
            }
        }
    }

    // Stop Timer for the task and update timeSpent
    public void stopTimer(Long taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            if (!task.isCompleted() && startTime != null) {
                int elapsedTime = (int) java.time.Duration.between(startTime, LocalDateTime.now()).toMinutes();
                task.setTimeSpent(task.getTimeSpent() + elapsedTime);  // Add elapsed time to current timeSpent
                taskRepository.save(task);
            }
        }
    }

    // Reset Timer (optional, in case you want to clear the timer)
    public void resetTimer(Long taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setTimeSpent(0);  // Reset the time spent to 0
            taskRepository.save(task);
        }
    }
}
