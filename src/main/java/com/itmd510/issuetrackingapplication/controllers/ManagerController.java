package com.itmd510.issuetrackingapplication.controllers;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class ManagerController {

    private Stage stage; // Reference to the stage
    private LoginController loginController;

    // Setter method for the stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogout() {
        // Implement logout logic
        showAlert();

        // Load the login screen
        loadLoginScreen();
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Logout clicked");
        alert.showAndWait();
    }

    private void loadLoginScreen() {
        try {
            // Load the FXML file for the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itmd510/issuetrackingapplication/views/LoginView.fxml"));
            Parent root = loader.load();

            // Create a new scene with the login screen
            Scene scene = new Scene(root);

            // Set the scene to the stage
            stage.setScene(scene);

            // Get the controller instance for the login screen
            LoginController loginController = loader.getController();

            // Set the stage reference in the login controller
            loginController.setStage(stage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}
