package org.example.client.controllers;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import org.example.client.MainApp;
import org.example.client.services.BackendService;
import org.example.client.services.UserService;
import org.example.client.utility.SessionData;

public class UserController {

    private final UserService userService = new UserService();
    private final BackendService backendService = new BackendService();

    public void openUpdateCredentialsDialog() {
        try {
            // Pre-fill current user details from SessionData
            String currentEmail = SessionData.getLoggedInUserEmail();
            Long userId = backendService.getSavedUserId();

            // Create dialog
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Update Credentials");
            dialog.setHeaderText("Edit your details:");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Current and new field inputs
            TextField oldEmailField = new TextField(currentEmail);
            oldEmailField.setEditable(false);
            TextField newEmailField = new TextField(currentEmail);

            TextField usernameField = new TextField();
            usernameField.setPromptText("Enter new username");

            PasswordField newPasswordField = new PasswordField();
            newPasswordField.setPromptText("Enter new password");

            grid.add(new Label("Current Email:"), 0, 0);
            grid.add(oldEmailField, 1, 0);
            grid.add(new Label("New Email:"), 0, 1);
            grid.add(newEmailField, 1, 1);
            grid.add(new Label("New Username:"), 0, 2);
            grid.add(usernameField, 1, 2);
            grid.add(new Label("New Password:"), 0, 3);
            grid.add(newPasswordField, 1, 3);

            dialog.getDialogPane().setContent(grid);

            // Add Save and Cancel buttons
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    String newEmail = newEmailField.getText();
                    String newUsername = usernameField.getText();
                    String newPassword = newPasswordField.getText();

                    if (validateInput(newEmail, newUsername, newPassword)) {
                        boolean success = userService.updateUser(
                                userId,
                                newUsername.isEmpty() ? null : newUsername,
                                newEmail.isEmpty() ? currentEmail : newEmail,
                                newPassword.isEmpty() ? null : newPassword
                        );

                        if (success) {
                            // Update session data if changes are made
                            SessionData.setLoggedInUserEmail(newEmail.isEmpty() ? currentEmail : newEmail);
                            if (!newPassword.isEmpty()) {
                                SessionData.setLoggedInPassword(newPassword);
                            }
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Credentials updated successfully.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update credentials. Ensure the email is unique.");
                        }
                    }
                }
                return null;
            });

            dialog.showAndWait();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating credentials.");
        }
    }

    private boolean validateInput(String email, String username, String password) {
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Email cannot be empty.");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void handleLogout() {
        try {
            MainApp.setScene("/org/example/client/login-view.fxml"); // Navigate back to the Login view
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
