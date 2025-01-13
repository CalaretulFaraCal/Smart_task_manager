package org.example.client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.geometry.Insets;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

import org.example.client.models.Phase;
import org.example.client.models.Project;
import org.example.client.models.Subtask;
import org.example.client.models.Task;
import org.example.client.services.BackendService;
import org.json.JSONObject;

public class TaskController {

    //VAR AND BUTTONS
    private final BackendService backendService = new BackendService();
    private Map<String, Project> projectMap = new HashMap<>();
    private String lastSelectedProjectTitle = null;  // Field to store last selected project
    @FXML private TextField txtSearchTask; // Matches fx:id in FXML
    @FXML private ComboBox<String> priorityComboBox; // Matches fx:id in FXML
    @FXML private ComboBox<String> cmbProjects;
    @FXML private Button btnAllTasks; // Matches fx:id in FXML
    @FXML private Button btnManageProjects;
    @FXML private Button btnUser;
    @FXML private Button btnAddTask;
    @FXML private Button btnRefresh;
    @FXML private VBox toDoColumn;
    @FXML private VBox inProgressColumn;
    @FXML private VBox doneColumn;
    @FXML private VBox overdueColumn;

    @FXML public void initialize() {
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
        cmbProjects.setOnAction(event -> {
            try {
                handleProjectSelection();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        btnManageProjects.setOnAction(event -> handleManageProjects());
        loadProjects();
    }

    //USERS
    @FXML private void showUserOptions() {
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

    //TASKS
    @FXML private void refreshTaskView() {
        try {
            List<Task> tasks = List.of();

            // Retrieve tasks based on selected project
            if (lastSelectedProjectTitle != null && !lastSelectedProjectTitle.equals("All")) {
                Project selectedProject = projectMap.get(lastSelectedProjectTitle);
                if (selectedProject != null) {
                    tasks = backendService.getTasksForProject(selectedProject.getId());
                }
            }
            else {
                tasks = backendService.getTasksForLoggedInUser();
            }

            // Apply search filter
            String searchQuery = txtSearchTask.getText().toLowerCase().trim();
            if (!searchQuery.isEmpty()) {
                tasks = tasks.stream()
                        .filter(task -> task.getTitle().toLowerCase().contains(searchQuery))
                        .collect(Collectors.toList());
            }

            // Apply priority filter
            String selectedPriority = priorityComboBox.getValue();
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
    @FXML private void handleAddTask() {
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
        priorityComboBox.getItems().addAll("All", "High", "Medium", "Low");  // Include "All"
        priorityComboBox.setValue("All");  // Default selection
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
        txtSearchTask.clear();
        priorityComboBox.setValue("All");
        cmbProjects.setValue("All");
        refreshTaskView();
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
            // Update the task's phase to COMPLETED
            task.setPhase(Phase.COMPLETED);

            // Prepare the data for the backend update
            Map<String, Object> updatedFields = new HashMap<>();
            updatedFields.put("phase", Phase.COMPLETED.toString());

        try {
            backendService.updateTask(task.getId(), updatedFields);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        refreshTaskView();
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
    private Node createTaskNode(Task task) {
        VBox taskBox = new VBox(10); // Box for task info
        taskBox.getStyleClass().add("task-box"); // Use CSS class

        // Task title
        Label titleLabel = new Label(task.getTitle());
        titleLabel.getStyleClass().add("task-title");

        // Task priority
        Label priorityLabel = new Label("Priority: " + task.getPriority());
        if (task.getPriority().equalsIgnoreCase("High")) {
            priorityLabel.getStyleClass().add("priority-high");
        } else if (task.getPriority().equalsIgnoreCase("Medium")) {
            priorityLabel.getStyleClass().add("priority-medium");
        } else {
            priorityLabel.getStyleClass().add("priority-low");
        }

        // Task deadline
        Label deadlineLabel = new Label("Deadline: " + task.getDeadline());
        deadlineLabel.getStyleClass().add("task-deadline");

        // Detail Label
        Label detailsLabel = new Label("Details: " + task.getDescription());
        detailsLabel.getStyleClass().add("task-details");

        // Buttons
        HBox buttonBox = new HBox(10);
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Button completeButton = new Button(task.getPhase() == Phase.COMPLETED ? "Completed" : "Complete");
        Button detailsButton = new Button("Details");

        editButton.getStyleClass().add("action-button");
        deleteButton.getStyleClass().add("action-button");
        completeButton.getStyleClass().add("action-button");
        detailsButton.getStyleClass().add("action-button");

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

    //SUBTASKS
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

    //UTILS
    private void showAlert(Alert.AlertType alertType, String success, String s) {}
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
                Task task = (Task) source.getUserData();

                try {
                    if (cmbProjects.getValue() != null) {
                        String selectedProjectTitle = cmbProjects.getValue();
                        Project selectedProject = projectMap.get(selectedProjectTitle);

                        if (selectedProject != null) {
                            task.setProject(selectedProject); // Update project association
                        }
                    }

                    task.setPhase(newPhase);
                    Map<String, Object> updatedFields = new HashMap<>();
                    updatedFields.put("phase", newPhase.toString());

                    if (!backendService.updateTask(task.getId(), updatedFields)) {
                        throw new Exception("Failed to update task phase");
                    }

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
        header.getStyleClass().add("column-header"); // Use CSS class
        column.getChildren().add(header);
    }
    private boolean showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
    }


    //FILTERS
    @FXML private void handleAllTasksAction() {
        txtSearchTask.clear(); // Clear search input
        priorityComboBox.setValue("All"); // Reset priority filter to "All"
        cmbProjects.setValue(null);
        refreshTaskView(); // Refresh view to show all tasks
    }
    @FXML private void handlePriorityFilterAction() {
        refreshTaskView(); // Refresh tasks based on selected priority
    }
    @FXML private void handleSearchAction() {
        txtSearchTask.textProperty().addListener((observable, oldValue, newValue) -> refreshTaskView());
    }


    //PROJECTS
    @FXML private void handleManageProjects() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Manage Projects");

        VBox dialogContent = new VBox(10);
        dialogContent.setPadding(new Insets(10));

        // Add buttons for project management
        Button addProjectButton = new Button("Add Project");
        Button deleteProjectButton = new Button("Delete Project");
        Button assignTaskButton = new Button("Assign Task to Project");

        addProjectButton.setOnAction(event -> handleAddProject());
        deleteProjectButton.setOnAction(event -> handleDeleteProject());
        assignTaskButton.setOnAction(event -> handleAssignTaskToProject());

        dialogContent.getChildren().addAll(addProjectButton, deleteProjectButton, assignTaskButton);
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }
    @FXML public void handleProjectSelection() throws Exception {
        lastSelectedProjectTitle = cmbProjects.getValue();  // Store selected project title
        if (lastSelectedProjectTitle == null || lastSelectedProjectTitle.equals("All")) {
            refreshTaskView();  // Refresh tasks for "All"
        } else {
            Project selectedProject = projectMap.get(lastSelectedProjectTitle);  // Retrieve Project object
            if (selectedProject != null) {
                List<Task> projectTasks = backendService.getTasksForProject(selectedProject.getId());
                System.out.println("Tasks for project: " + projectTasks);
                populateTaskColumns(projectTasks);  // Update UI with tasks
            }
        }
    }
    @FXML public void handleAddProject() {
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
            // Use the backendService to save the project
            try {
                backendService.addProject(project, backendService.savedUserId); // Call the backend service
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });
        refreshProjectList();
    }
    public void handleAssignTaskToProject() {
        try {
            // Fetch tasks and projects
            List<Task> tasks = backendService.getTasksForLoggedInUser();
            List<Project> projects = backendService.getAllProjects();

            if (tasks.isEmpty() || projects.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "No tasks or projects available to assign.");
                return;
            }

            // Create a dialog
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Assign Task to Project");

            // ComboBoxes for selection
            ComboBox<Task> taskComboBox = new ComboBox<>();
            taskComboBox.getItems().addAll(tasks);
            taskComboBox.setPromptText("Select a task");

            ComboBox<Project> projectComboBox = new ComboBox<>();
            projectComboBox.getItems().addAll(projects);
            projectComboBox.setPromptText("Select a project");

            VBox content = new VBox(10, new Label("Task:"), taskComboBox, new Label("Project:"), projectComboBox);
            content.setPadding(new Insets(10));

            dialog.getDialogPane().setContent(content);

            // Add "Assign" and "Cancel" buttons
            ButtonType assignButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == assignButtonType) {
                    Task selectedTask = taskComboBox.getValue();
                    Project selectedProject = projectComboBox.getValue();

                    if (selectedTask != null && selectedProject != null) {
                        try {
                            backendService.assignTaskToProject(selectedProject.getId(), selectedTask);
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Task assigned to project successfully.");
                        } catch (Exception e) {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to assign task: " + e.getMessage());
                        }
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Warning", "Please select both a task and a project.");
                    }
                }
                return null;
            });

            // Show the dialog
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    public void handleDeleteProject() {
        try {
            // Fetch all projects
            List<Project> projects = backendService.getAllProjects();

            // Show a choice dialog to select a project for deletion
            ChoiceDialog<Project> dialog = new ChoiceDialog<>(projects.get(0), projects);
            dialog.setTitle("Delete Project");
            dialog.setHeaderText("Select a project to delete");

            Optional<Project> result = dialog.showAndWait();
            result.ifPresent(project -> {
                // Ask if tasks should be deleted
                boolean deleteTasks = showConfirmationDialog(
                        "Delete Tasks",
                        "Do you also want to delete all tasks associated with this project?"
                );

                try {
                    // Call backend to delete the project with the user's choice for tasks
                    backendService.deleteProject(project.getId(), deleteTasks);

                    System.out.println("Project deleted successfully with tasks: " + deleteTasks);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Project deleted successfully.");

                    // Refresh the project list and task view
                    refreshProjectList();
                    refreshTaskView();

                } catch (Exception e) {
                    System.err.println("Error deleting project: " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Error", "Error deleting project: " + e.getMessage());
                }
            });
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch project list.");
        }
    }
    private void refreshProjectList() {
        try {
            // Fetch all projects from the backend
            List<Project> projects = backendService.getAllProjects();

            // Clear the existing items in the ComboBox
            cmbProjects.getItems().clear();

            // Add a default "All" option
            cmbProjects.getItems().add("All");

            // Populate ComboBox with project titles
            projectMap.clear(); // Ensure the map is cleared to avoid stale data
            for (Project project : projects) {
                cmbProjects.getItems().add(project.getTitle());
                projectMap.put(project.getTitle(), project); // Map project title to Project object
            }

            // Optionally select the "All" option by default
            cmbProjects.setValue("All");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to refresh project list.");
        }
    }
    private void loadProjects() {
        try {
            List<Project> projects = backendService.getAllProjects(); // Fetch projects
            ObservableList<String> projectNames = FXCollections.observableArrayList();

            projectNames.add("Select Project"); // Add default option for resetting
            for (Project project : projects) {
                projectNames.add(project.getTitle());
                projectMap.put(project.getTitle(), project); // Map title to Project
            }

            cmbProjects.setItems(projectNames); // Set the combo box items
            cmbProjects.setValue("Select Project"); // Set default selection
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); // Handle errors
        }
    }

}