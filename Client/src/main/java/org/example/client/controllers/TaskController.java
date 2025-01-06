package org.example.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.example.client.models.Phase;
import org.example.client.models.Subtask;
import org.example.client.models.Task;
import org.example.client.services.BackendService;
import javafx.geometry.Insets;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;


public class TaskController {

    @FXML
    private TextField txtSearchTask; // Matches fx:id in FXML

    @FXML
    private Button btnAllTasks; // Matches fx:id in FXML

    @FXML
    private ComboBox<String> priorityComboBox; // Matches fx:id in FXML

    @FXML
    private Button btnUser;

    @FXML
    private Button btnAddTask;

    @FXML
    private Button btnRefresh;

    @FXML
    private VBox toDoColumn;

    @FXML
    private VBox inProgressColumn;

    @FXML
    private VBox doneColumn;

    @FXML
    private VBox overdueColumn;

    private final BackendService backendService = new BackendService();

    @FXML
    public void initialize() {
        try {
            // Fetch tasks for the logged-in user
            List<Task> tasks = backendService.getTasksForLoggedInUser();

            // Populate UI columns based on fetched tasks
            populateTaskColumns(tasks);

            // Enable drag-and-drop for all columns
            enableDrag(toDoColumn);
            enableDrag(inProgressColumn);
            enableDrag(doneColumn);
            enableDrag(overdueColumn);

            enableDrop(toDoColumn, Phase.NOT_STARTED);
            enableDrop(inProgressColumn, Phase.IN_PROGRESS);
            enableDrop(doneColumn, Phase.COMPLETED);
            enableDrop(overdueColumn, Phase.OVERDUE);

        } catch (Exception e) {
            System.err.println("Error fetching tasks: " + e.getMessage());
            e.printStackTrace();
        }

        // Set up button actions
        btnAddTask.setOnAction(event -> handleAddTask());
        btnRefresh.setOnAction(event -> refreshTaskView());
        btnUser.setOnAction(event -> showUserOptions());
        btnAllTasks.setOnAction(event -> handleAllTasksAction()); // Assuming `allTasksButton` is defined
        priorityComboBox.setOnAction(event -> handlePriorityFilterAction());
        txtSearchTask.textProperty().addListener((observable, oldValue, newValue) -> refreshTaskView());
    }

    @FXML
    private void showUserOptions() {
        // Create a context menu with options
        ContextMenu userOptionsMenu = new ContextMenu();
        MenuItem logoutItem = new MenuItem("Logout");
        MenuItem changeCredentialsItem = new MenuItem("Change Credentials");

        // Create an instance of UserController
        UserController userController = new UserController();

        // Delegate actions to UserController methods
        logoutItem.setOnAction(event -> userController.handleLogout());
        changeCredentialsItem.setOnAction(event -> userController.openUpdateCredentialsDialog());

        userOptionsMenu.getItems().addAll(logoutItem, changeCredentialsItem);

        // Show the menu at the button's position
        userOptionsMenu.show(
                btnUser,
                btnUser.getScene().getWindow().getX() + btnUser.getLayoutX(),
                btnUser.getScene().getWindow().getY() + btnUser.getLayoutY() + btnUser.getHeight()
        );
    }

    @FXML
    private void handleAddTask() {
        // Create a dialog for task creation
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Add Task");
        dialog.setHeaderText("Enter Task Details");

        // Set up form layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Input fields
        TextField titleField = new TextField();
        TextField descriptionField = new TextField();
        TextField categoryField = new TextField();
        ComboBox<String> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll("All","Low", "Medium", "High");
        priorityBox.setValue("All"); // Default to showing all tasks
        DatePicker deadlinePicker = new DatePicker();

        // Add inputs to the grid
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryField, 1, 2);
        grid.add(new Label("Priority:"), 0, 3);
        grid.add(priorityBox, 1, 3);
        grid.add(new Label("Deadline:"), 0, 4);
        grid.add(deadlinePicker, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Task task = new Task();
                task.setTitle(titleField.getText());
                task.setDescription(descriptionField.getText());
                task.setCategory(categoryField.getText());
                task.setPriority(priorityBox.getValue());
                task.setDeadline(deadlinePicker.getValue() != null ? deadlinePicker.getValue().toString() : null);
                return task;
            }
            return null;
        });

        Long userId = backendService.savedUserId;
        Optional<Task> result = dialog.showAndWait();
        result.ifPresent(task -> {
            try {
                backendService.addTask(task, userId);
                System.out.println("Task added successfully.");
                refreshTaskView();
            } catch (Exception e) {
                System.err.println("Error adding task: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void handleEditTask(Task task) {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Edit Task");
        dialog.setHeaderText("Modify Task Details");

        // Input fields for editing task details
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(task.getTitle());
        DatePicker deadlinePicker = new DatePicker(LocalDate.parse(task.getDeadline()));
        ComboBox<String> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll("High", "Medium", "Low");
        priorityBox.setValue(task.getPriority());

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Deadline:"), 0, 1);
        grid.add(deadlinePicker, 1, 1);
        grid.add(new Label("Priority:"), 0, 2);
        grid.add(priorityBox, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                task.setTitle(titleField.getText());
                task.setDeadline(deadlinePicker.getValue().toString());
                task.setPriority(priorityBox.getValue());
                return task;
            }
            return null;
        });

        Optional<Task> result = dialog.showAndWait();
        result.ifPresent(updatedTask -> {
            try {
                boolean success = backendService.updateTask(updatedTask.getId(), convertTaskToMap(updatedTask));
                if (success) {
                    System.out.println("Task updated successfully.");
                    refreshTaskView();
                } else {
                    System.err.println("Failed to update task.");
                }
            } catch (Exception e) {
                System.err.println("Error updating task: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void handleMarkAsComplete(Task task) {
        try {
            // Update the task's phase to COMPLETED
            task.setPhase(Phase.COMPLETED);

            // Prepare the data for the backend update
            Map<String, Object> updatedFields = new HashMap<>();
            updatedFields.put("phase", Phase.COMPLETED.toString());

            // Send the update to the backend
            boolean success = backendService.updateTask(task.getId(), updatedFields);

            if (success) {
                System.out.println("Task marked as completed.");
                refreshTaskView(); // Refresh the task view to reflect the change
            } else {
                System.err.println("Failed to mark task as completed.");
            }
        } catch (Exception e) {
            System.err.println("Error marking task as complete: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteTask(Task task) {
        // Confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Task");
        alert.setHeaderText("Are you sure you want to delete this task?");
        alert.setContentText("Task: " + task.getTitle());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = backendService.deleteTask(task.getId()); // Call BackendService
                if (success) {
                    System.out.println("Task deleted successfully.");
                    refreshTaskView();
                } else {
                    System.err.println("Failed to delete task.");
                }
            } catch (Exception e) {
                System.err.println("Error deleting task: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showTaskDetails(Task task) {
        // Create Dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Task Details");
        dialog.setResizable(true);

        // Root layout: VBox
        VBox dialogContent = new VBox(20);
        dialogContent.setPadding(new Insets(20));
        dialogContent.setPrefSize(600, 400); // Adjust size for larger dialog

        // Top Section: Task Details
        VBox taskDetailsBox = new VBox(10);
        taskDetailsBox.setPadding(new Insets(10));
        taskDetailsBox.setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-background-color: lightgray;");
        Label taskDetails = new Label(
                "Title: " + task.getTitle() + "\n" +
                        "Priority: " + task.getPriority() + "\n" +
                        "Deadline: " + task.getDeadline() + "\n" +
                        "Description: " + task.getDescription()
        );
        taskDetails.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        taskDetailsBox.getChildren().add(taskDetails);

        // Add Subtask Button
        Button addSubtaskButton = new Button("Add Subtask");
        addSubtaskButton.setOnAction(event -> {
            showAddSubtaskDialog(task); // Call the add dialog
            refreshSubtaskList(task, new FlowPane());  // Refresh subtasks after adding
        });

        taskDetailsBox.getChildren().add(addSubtaskButton);

        // Bottom Section: Subtask List
        ScrollPane subtaskScrollPane = new ScrollPane();
        subtaskScrollPane.setFitToWidth(true);

        FlowPane subtaskPane = new FlowPane();
        subtaskPane.setHgap(10);
        subtaskPane.setVgap(10);
        subtaskPane.setPadding(new Insets(10));
        subtaskScrollPane.setContent(subtaskPane);

        // Load Subtasks Initially
        refreshSubtaskList(task, subtaskPane);

        // Add sections to the root layout
        dialogContent.getChildren().addAll(taskDetailsBox, subtaskScrollPane);
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    private void refreshSubtaskList(Task task, FlowPane subtaskList) {
        subtaskList.getChildren().clear(); // Clear previous content

        List<Subtask> subtasks = backendService.getSubtasksForTask(task.getId());
        for (Subtask subtask : subtasks) {
            HBox subtaskBox = createSubtaskBox(subtask, task);
            subtaskList.getChildren().add(subtaskBox);
        }
    }

    private HBox createSubtaskBox(Subtask subtask, Task task) {
        Label subtaskDetails = new Label(
                "Title: " + subtask.getTitle() + "\n" +
                        "Description: " + subtask.getDescription()
        );
        subtaskDetails.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-padding: 10;");

        HBox subtaskBox = new HBox(subtaskDetails);
        subtaskBox.setPadding(new Insets(10));
        subtaskBox.setStyle(subtask.isCompleted()
                ? "-fx-background-color: green; -fx-border-radius: 5;"
                : "-fx-background-color: red; -fx-border-radius: 5;");

        // Context Menu for Subtask
        ContextMenu contextMenu = new ContextMenu();
        MenuItem toggleCompletionItem = new MenuItem(subtask.isCompleted() ? "Mark as Incomplete" : "Mark as Complete");
        toggleCompletionItem.setOnAction(event -> {
            try {
                subtask.setCompleted(!subtask.isCompleted());
                boolean success = backendService.updateSubtaskCompletion(task.getId(), subtask.getId(), subtask.isCompleted());
                if (success) {
                    refreshSubtaskList(task, (FlowPane) subtaskBox.getParent());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update subtask.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update subtask.");
            }
        });
        contextMenu.getItems().add(toggleCompletionItem);
        subtaskBox.setOnContextMenuRequested(event -> contextMenu.show(subtaskBox, event.getScreenX(), event.getScreenY()));

        return subtaskBox;
    }

    @FXML
    private void refreshTaskView() {
        try {
            // Retrieve all tasks for the logged-in user
            List<Task> tasks = backendService.getTasksForLoggedInUser();

            // Apply search filter
            String searchQuery = txtSearchTask.getText().toLowerCase().trim(); // Assuming `searchBar` is a TextField
            if (!searchQuery.isEmpty()) {
                tasks = tasks.stream()
                        .filter(task -> task.getTitle().toLowerCase().contains(searchQuery))
                        .collect(Collectors.toList());
            }

            // Apply priority filter
            String selectedPriority = priorityComboBox.getValue(); // Assuming `priorityFilter` is a ComboBox
            if (selectedPriority != null && !"All".equals(selectedPriority)) {
                tasks = tasks.stream()
                        .filter(task -> task.getPriority().equalsIgnoreCase(selectedPriority))
                        .collect(Collectors.toList());
            }

            // Populate task columns with filtered tasks
            populateTaskColumns(tasks);
        } catch (Exception e) {
            System.err.println("Error refreshing task view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Node createTaskNode(Task task) {
        VBox taskBox = new VBox(10); // Box for task info
        taskBox.setPadding(new Insets(10));
        taskBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #000000; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Task title
        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333333;");

        // Task priority
        Label priorityLabel = new Label("Priority: " + task.getPriority());
        if (task.getPriority().equalsIgnoreCase("High")) {
            priorityLabel.setStyle("-fx-text-fill: red;");
        } else if (task.getPriority().equalsIgnoreCase("Medium")) {
            priorityLabel.setStyle("-fx-text-fill: orange;");
        } else {
            priorityLabel.setStyle("-fx-text-fill: green;");
        }

        // Task deadline
        Label deadlineLabel = new Label("Deadline: " + task.getDeadline());
        deadlineLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");

        // Detail Label
        Label detailsLabel = new Label("Details: " + task.getDescription());
        detailsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;");

        // Buttons
        HBox buttonBox = new HBox(10);
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Button completeButton = new Button(task.getPhase() == Phase.COMPLETED ? "Completed" : "Complete");
        Button detailsButton = new Button("Details");

        editButton.setStyle("-fx-font-size: 12px; -fx-padding: 5; -fx-background-radius: 1em;");
        deleteButton.setStyle("-fx-font-size: 12px; -fx-padding: 5; -fx-background-radius: 1em;");
        completeButton.setStyle("-fx-font-size: 12px; -fx-padding: 5; -fx-background-radius: 1em;");
        detailsButton.setStyle("-fx-font-size: 12px; -fx-padding: 5; -fx-background-radius: 1em;");

        editButton.setOnAction(event -> handleEditTask(task));
        deleteButton.setOnAction(event -> handleDeleteTask(task));
        completeButton.setOnAction(event -> handleMarkAsComplete(task));
        detailsButton.setOnAction(event -> showTaskDetails(task));

        buttonBox.getChildren().addAll(editButton, deleteButton, completeButton, detailsButton);
        taskBox.getChildren().addAll(titleLabel, priorityLabel, deadlineLabel, detailsLabel, buttonBox);

        // Attach the task object to the node for drag-and-drop support
        taskBox.setUserData(task);

        return taskBox;
    }

    private void showAddSubtaskDialog(Task task) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Subtask");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 150, 30, 30));

        TextField titleField = new TextField();
        TextField descriptionField = new TextField();
        TextField completeField = new TextField();

        grid.add(new Label("Subtask Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    // Construct JSON dynamically
                    JSONObject subtaskJson = new JSONObject();
                    subtaskJson.put("title", titleField.getText());
                    subtaskJson.put("description", descriptionField.getText());

                    boolean success = backendService.createSubtask(task.getId(), subtaskJson.toString());
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Subtask added successfully.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to add subtask.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Unable to add subtask.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String success, String s) {}

    public void populateTaskColumns(List<Task> tasks) {
            // Clear columns before repopulating
            toDoColumn.getChildren().clear();
            doneColumn.getChildren().clear();
            overdueColumn.getChildren().clear();
            inProgressColumn.getChildren().clear();

            // Add headers back to each column
            addColumnHeader(toDoColumn, "To Do");
            addColumnHeader(doneColumn, "Done");
            addColumnHeader(overdueColumn, "Overdue");
            addColumnHeader(inProgressColumn, "In Progress");

            // Populate tasks into columns based on their Phase
            for (Task task : tasks) {
                // Create a VBox for the task
                Node taskNode = createTaskNode(task);


                // Assign the task to the appropriate column based on its Phase
                switch (task.getPhase()) {
                    case NOT_STARTED:
                        toDoColumn.getChildren().add(taskNode);
                        break;
                    case IN_PROGRESS:
                        inProgressColumn.getChildren().add(taskNode);
                        break;
                    case COMPLETED:
                        doneColumn.getChildren().add(taskNode);
                        break;
                    case OVERDUE:
                        overdueColumn.getChildren().add(taskNode);
                        break;
                }
            }
    }

    private void enableDrag(VBox column) {
        for (Node child : column.getChildren()) {
            child.setOnDragDetected(event -> {
                Dragboard db = child.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("Task");
                db.setContent(content);
                event.consume();
            });
        }
    }

    private void enableDrop(VBox column, Phase newPhase) {
        column.setOnDragOver(event -> {
            if (event.getGestureSource() != column && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        column.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                Node source = (Node) event.getGestureSource();
                Task task = (Task) source.getUserData(); // Task object should be set as user data on the Node

                try {
                    // Update task phase in the backend
                    Map<String, Object> updatedFields = new HashMap<>();
                    updatedFields.put("phase", newPhase.toString()); // Send only the phase field

                    boolean success = backendService.updateTask(task.getId(), updatedFields);
                    if (!success) {
                        throw new Exception("Failed to update task phase");
                    }

                    // Update the UI
                    task.setPhase(newPhase);
                    ((VBox) source.getParent()).getChildren().remove(source);
                    column.getChildren().add(source);

                    event.setDropCompleted(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    event.setDropCompleted(false);
                }
            }
            event.consume();
        });
    }

    private void addColumnHeader(VBox column, String title) {
        Label header = new Label(title);
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10px;");
        column.getChildren().add(header);
    }

    private Map<String, Object> convertTaskToMap(Task task) {
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("id", task.getId());
        taskMap.put("title", task.getTitle());
        taskMap.put("description", task.getDescription());
        taskMap.put("priority", task.getPriority());
        taskMap.put("deadline", task.getDeadline());
        taskMap.put("phase", task.getPhase().toString()); // Use the `phase` field instead of `completed`
        return taskMap;
    }

    @FXML
    private void handleAllTasksAction() {
        txtSearchTask.clear(); // Clear search input
        priorityComboBox.setValue("All"); // Reset priority filter to "All"
        refreshTaskView(); // Refresh view to show all tasks
    }

    @FXML
    private void handlePriorityFilterAction() {
        refreshTaskView(); // Refresh tasks based on selected priority
    }

    @FXML
    private void handleSearchAction() {
        txtSearchTask.textProperty().addListener((observable, oldValue, newValue) -> refreshTaskView());
    }

}
