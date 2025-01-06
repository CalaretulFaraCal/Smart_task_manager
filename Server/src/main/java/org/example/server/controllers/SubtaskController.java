package org.example.server.controllers;

import org.example.server.dto.SubtaskCreateRequest;
import org.example.server.models.Subtask;
import org.example.server.services.SubtaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subtask")
public class SubtaskController {

    @Autowired
    private SubtaskService subtaskService;
    private Subtask subtask;

    // Create a subtask for a parent task
    @PostMapping("/{parentTaskId}")
    public ResponseEntity<Subtask> createSubtask(@PathVariable Long parentTaskId, @RequestBody SubtaskCreateRequest request) {
        System.out.println(parentTaskId + "in controler");
        Subtask newSubtask = subtaskService.createSubtask(parentTaskId, request);
        System.out.println(parentTaskId + "in controler_2");
        return ResponseEntity.status(HttpStatus.CREATED).body(newSubtask);
    }

    // Fetch subtasks for a parent task
    @GetMapping("/{parentTaskId}")
    public ResponseEntity<List<Subtask>> getSubtasks(@PathVariable Long parentTaskId) {
        List<Subtask> subtasks = subtaskService.getSubtasksByParentTaskId(parentTaskId);
        return ResponseEntity.ok(subtasks);
    }

    @PutMapping("/{parentTaskId}/{subtaskId}")
    public ResponseEntity<Subtask> updateSubtask(@PathVariable Long parentTaskId, @PathVariable Long subtaskId, @RequestBody Map<String, Object> subtaskData) {
        // Extract subtask ID and completed field from the request body
        // Extract the "completed" status from the request body
        Boolean completed = (Boolean) subtaskData.get("completed");

        // Fetch the subtask from the service
        Subtask subtask = subtaskService.getSubtaskById(subtaskId);

        // Validate if the subtask exists and belongs to the correct parent task
        if (subtask == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        if (!subtask.getParentTask().getId().equals(parentTaskId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Update the subtask's completed status
        subtask.setCompleted(completed);

        // Save the updated subtask
        Subtask updatedSubtask = subtaskService.saveSubtask(subtask);

        return ResponseEntity.ok(updatedSubtask);

    }
}
