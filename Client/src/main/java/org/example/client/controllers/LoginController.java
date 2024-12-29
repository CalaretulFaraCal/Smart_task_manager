package org.example.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import org.example.client.services.UserService;

public class LoginController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        UserService userService = new UserService();
        if (userService.validateLogin(email, password)) {
            statusLabel.setText("Login successful.");
        } else {
            statusLabel.setText("Invalid email or password.");
        }
    }
}

