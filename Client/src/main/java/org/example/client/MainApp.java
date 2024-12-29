package org.example.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        setScene("login-view.fxml"); // Start with the Login view
        primaryStage.setTitle("Smart Task Manager");
        primaryStage.show();
    }

    public static void setScene(String fxml) throws IOException {
        // Attempt to locate the resource file
        URL resource = MainApp.class.getResource(fxml);
        if (resource == null) {
            // If the file isn't found, print an error and throw an exception
            System.out.println("FXML file not found for path: " + fxml);
            throw new IOException("FXML file not found: " + fxml);
        }

        // If found, print the file location
        System.out.println("FXML file found: " + resource);

        // Load the FXML file and set the scene
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}
