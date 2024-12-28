package org.example.server.controllers;

import org.example.server.dto.SubtaskCreateRequest;
import org.example.server.services.SubtaskService;
import org.example.server.models.Subtask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/task/{taskId}/subtask")
public class SubtaskController {

    @Autowired
    private SubtaskService subtaskService;

    @PostMapping
    public ResponseEntity<Subtask> createSubtask(@PathVariable Long taskId, @RequestBody @Valid SubtaskCreateRequest subtaskCreateRequest) {
        // Calling the service with the request object
        Subtask subtask = subtaskService.createSubtask(taskId, subtaskCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(subtask);
    }

    // You can add additional endpoints for updating, deleting, fetching, etc.
}

