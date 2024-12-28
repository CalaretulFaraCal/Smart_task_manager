package org.example.server.controllers;

import org.example.server.services.TimerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task/{taskId}/timer")
public class TimerController {

    private final TimerService timerService;

    public TimerController(TimerService timerService) {
        this.timerService = timerService;
    }

    // Start Timer
    @PostMapping("/start")
    public ResponseEntity<String> startTimer(@PathVariable Long taskId) {
        timerService.startTimer(taskId);
        return ResponseEntity.status(HttpStatus.OK).body("Timer started for task ID " + taskId);
    }

    // Stop Timer
    @PostMapping("/stop")
    public ResponseEntity<String> stopTimer(@PathVariable Long taskId) {
        timerService.stopTimer(taskId);
        return ResponseEntity.status(HttpStatus.OK).body("Timer stopped for task ID " + taskId);
    }

    // Reset Timer (optional)
    @PostMapping("/reset")
    public ResponseEntity<String> resetTimer(@PathVariable Long taskId) {
        timerService.resetTimer(taskId);
        return ResponseEntity.status(HttpStatus.OK).body("Timer reset for task ID " + taskId);
    }
}
