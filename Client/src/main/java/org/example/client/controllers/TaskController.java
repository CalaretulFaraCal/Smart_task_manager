package org.example.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class TaskController {

    @FXML
    private ListView<String> taskListView;

    @FXML
    private TextField taskTextField;

    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    @FXML
    private void handleAddTask() {
        String task = taskTextField.getText();
        if (!task.isEmpty()) {
            taskListView.getItems().add(task);
            taskTextField.clear();
        }
    }

    @FXML
    private void handleRemoveTask() {
        String selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskListView.getItems().remove(selectedTask);
        }
    }
}
