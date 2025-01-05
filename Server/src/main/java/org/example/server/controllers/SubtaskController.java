package org.example.server.controllers;

import org.example.server.dto.SubtaskCreateRequest;
import org.example.server.models.Subtask;
import org.example.server.services.SubtaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subtask")
public class SubtaskController {

    @Autowired
    private SubtaskService subtaskService;

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

}
