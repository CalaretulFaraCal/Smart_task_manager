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
        setScene("/org/example/client/login-view.fxml"); // Start with the Login view
        primaryStage.setTitle("Smart Task Manager");
        primaryStage.show();
    }

    public static void setScene(String fxml) throws IOException {
        // Attempt to locate the resource file
        URL resource = MainApp.class.getResource(fxml);
        if (resource == null) {
            System.out.println("FXML file not found for path: " + fxml);
            throw new IOException("FXML file not found: " + fxml);
        }

        // Load the FXML file and create the scene
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        Scene newScene = new Scene(fxmlLoader.load());

        // Load and attach the CSS file to the scene
        URL cssResource = MainApp.class.getResource("/styles/styles.css");
        if (cssResource != null) {
            newScene.getStylesheets().add(cssResource.toExternalForm());
        } else {
            System.out.println("CSS file not found at /styles/styles.css");
        }

        // Set the scene to the primary stage
        primaryStage.setScene(newScene);
    }

    public static void main(String[] args) {
        launch();
    }
}
