package org.example.client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.client.models.Project;
import org.example.client.models.Task;
import org.example.client.services.BackendService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProjectController {

    @FXML private static ComboBox<String> cmbProjects;

    private static final BackendService backendService = new BackendService();
    private static final Map<String, Project> projectMap = new HashMap<>();

    public static void handleAddProject() {
        Dialog<Project> dialog = new Dialog<>();
        dialog.setTitle("Add New Project");

        // Create input fields dynamically
        TextField titleField = new TextField();
        titleField.setPromptText("Project Title");

        VBox dialogContent = new VBox(10, new Label("Title:"), titleField);
        dialogContent.setPadding(new Insets(10)); // Add padding to make the dialog look nicer
        dialog.getDialogPane().setContent(dialogContent);

        // Add buttons to the dialog
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Handle the result of the dialog
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String title = titleField.getText();
                if (title.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Project title cannot be empty.");
                    return null;
                }
                return new Project(title); // Assuming Project has a constructor that accepts a title
            }
            return null;
        });

        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(project -> {
            System.out.println("New project created: " + project.getTitle());
            // Add your logic here to save the project (e.g., send to backend or update UI)
        });
    }

    private static void showAlert(Alert.AlertType alertType, String error, String s) {}

    public static void handleDeleteProject() {
        try {
            // Fetch all projects
            List<Project> projects = backendService.getAllProjects();

            ChoiceDialog<Project> dialog = new ChoiceDialog<>(projects.get(0), projects);
            dialog.setTitle("Delete Project");
            dialog.setHeaderText("Select a project to delete");

            Optional<Project> result = dialog.showAndWait();
            result.ifPresent(project -> {
                try {
                    backendService.deleteProject(project.getId(), false); // Optionally delete tasks
                    System.out.println("Project deleted successfully.");
                } catch (Exception e) {
                    System.err.println("Error deleting project: " + e.getMessage());
                }
            });
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void handleAssignTaskToProject() {
        try {
            List<Task> tasks = backendService.getTasksForLoggedInUser();
            List<Project> projects = backendService.getAllProjects();

            ComboBox<Task> taskComboBox = new ComboBox<>();
            taskComboBox.getItems().addAll(tasks);

            ComboBox<Project> projectComboBox = new ComboBox<>();
            projectComboBox.getItems().addAll(projects);

            // Logic to assign task to selected project
            Task selectedTask = taskComboBox.getValue();
            Project selectedProject = projectComboBox.getValue();

            if (selectedTask != null && selectedProject != null) {
                backendService.assignTaskToProject(selectedProject.getId(), selectedTask);
                System.out.println("Task assigned to project successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void loadProjects() {
        try {
            List<Project> projects = backendService.getAllProjects(); // Fetch projects
            ObservableList<String> projectNames = FXCollections.observableArrayList();

            for (Project project : projects) {
                projectNames.add(project.getTitle());
                projectMap.put(project.getTitle(), project); // Map title to Project
            }

            cmbProjects.setItems(projectNames); // Set the combo box items
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); // Handle errors
        }
    }


}