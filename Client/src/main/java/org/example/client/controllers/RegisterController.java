package org.example.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.client.MainApp;
import org.example.client.services.UserService;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    private final UserService userService = new UserService();

    @FXML
    public void onRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        boolean success = userService.register(username, email, password);

        if (success == true) {
            showAlert("Success", "Registration successful!");
            try {
                MainApp.setScene("/org/example/client/task-view.fxml"); // Navigate back to the Login view
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load the login view.");
            }
        } else {
            showAlert("Error", "Registration failed. Email may already be in use.");
        }
    }

    @FXML
    public void onLogin() {
        try {
            MainApp.setScene("login-view.fxml"); // Switch to the Login scene
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the login view.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
