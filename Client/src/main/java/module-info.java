module org.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;
   // requires validatorfx;

    opens org.example.client.controllers to javafx.fxml;
    exports org.example.client;
    exports org.example.client.controllers;
}
