package org.example.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import org.example.client.models.Task;
import org.example.client.services.TaskService;

import java.util.List;

public class TaskController {

    @FXML
    private ListView<String> taskListView;

    @FXML
    private ComboBox<Task> parentTaskComboBox;

    private TaskService taskService;

    private String loggedInUserEmail; // Store the email of the logged-in user

    public TaskController() {
        this.taskService = new TaskService(); // Initialize TaskService
    }

    @FXML
    public void initialize() {
        // Attempt to load tasks for the user
        loadTasksForUser();
    }

    public void setLoggedInUserEmail(String email) {
        this.loggedInUserEmail = email;
        System.out.println("Logged in user email set to: " + email);
    }

    public void loadTasksForUser() {
        try {
            if (loggedInUserEmail == null || loggedInUserEmail.isEmpty()) {
                showAlert("Error", "User email is not set. Cannot load tasks.");
                return;
            }

            // Fetch tasks for the logged-in user
            List<Task> tasks = taskService.getTasksForUser(loggedInUserEmail);

            // Clear existing task list
            taskListView.getItems().clear();
            parentTaskComboBox.getItems().clear();

            // Add tasks to the UI components
            for (Task task : tasks) {
                String taskDisplay = String.format("%s - %s", task.getTitle(), task.getDeadline());
                taskListView.getItems().add(taskDisplay);
                parentTaskComboBox.getItems().add(task);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load tasks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
