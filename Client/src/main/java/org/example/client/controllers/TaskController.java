package org.example.client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.client.models.Task;
import org.example.client.services.BackendService;

import java.util.List;

public class TaskController {

    @FXML
    private TableView<Task> taskTable;

    @FXML
    private TableColumn<Task, String> titleColumn;

    @FXML
    private TableColumn<Task, String> deadlineColumn;

    @FXML
    private TableColumn<Task, String> priorityColumn;

    @FXML
    private TableColumn<Task, Boolean> completedColumn;

    private BackendService backendService = new BackendService();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        completedColumn.setCellValueFactory(new PropertyValueFactory<>("completed"));

        try {
            // Fetch tasks from the backend
            List<Task> tasks = backendService.getTasksForLoggedInUser();

            // Print tasks to console
            System.out.println("Fetched Tasks:");
            tasks.forEach(task -> System.out.println(task));

            // Bind tasks to TableView
            ObservableList<Task> taskObservableList = FXCollections.observableArrayList(tasks);
            taskTable.setItems(taskObservableList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void loadTasksForUser() {
        try {
            // Fetch tasks from backend service
            List<Task> tasks = backendService.getTasksForLoggedInUser();

            // Convert to ObservableList and bind to TableView
            ObservableList<Task> taskObservableList = FXCollections.observableArrayList(tasks);
            taskTable.setItems(taskObservableList);
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
