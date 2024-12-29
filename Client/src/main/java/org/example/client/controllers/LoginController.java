package org.example.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.client.MainApp;
import org.example.client.services.UserService;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private final UserService userService = new UserService();

    @FXML
    public void onLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Email and password cannot be empty.");
            return;
        }

        boolean success = userService.login(email, password);

        if (success) {
            try {
                // Use the absolute path to the FXML file
                MainApp.setScene("/org/example/client/task-view.fxml");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load the task view.");
            }
        }

    }

    @FXML
    public void onRegister() {
        try {
            MainApp.setScene("/org/example/client/register-view.fxml"); // Switch to the Register scene
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the register view.");
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
