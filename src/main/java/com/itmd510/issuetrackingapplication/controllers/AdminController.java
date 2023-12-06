package com.itmd510.issuetrackingapplication.controllers;

import com.itmd510.issuetrackingapplication.config.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {

    @FXML
    private Button manageUsersButton;

    @FXML
    private Button manageProjectsButton;

    @FXML
    private Button generateReportsButton;

    @FXML
    private Button logoutButton; // Added logout button

    private Stage stage;
    private LoginController loginController;


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initialize() {
        // Initialization logic can go here
    }

    @FXML
    private void handleManageUsers() {
        // Implement logic to navigate to the manage users page
        showAlert("Manage Users clicked");
    }

    @FXML
    private void handleManageProjects() {
        // Implement logic to navigate to the manage projects page
        showAlert("Manage Projects clicked");
    }

    @FXML
    private void handleGenerateReports() {
        // Implement logic to generate reports
        showAlert("Generate Reports clicked");
    }

    @FXML
    private void handleLogout() {
        // Check if the current user is logged in and log them out
        if (SessionManager.getInstance().isLoggedIn()) {
            SessionManager.getInstance().logout();
            loadLoginScreen();
        } else {
            showAlert("No user is currently logged in.");
        }
    }

    private void loadLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itmd510/issuetrackingapplication/views/LoginView.fxml"));
            Parent loginPane = loader.load();

            // Create a new scene with the login screen
            Scene loginScene = new Scene(loginPane, 500, 400);

            // Set the scene to the stage
            stage.setScene(loginScene);

            // Get the controller instance based on the loaded FXML
            Object controller = loader.getController();

            // If the controller is an instance of LoginController, set the stage reference
            if (controller instanceof LoginController) {
                ((LoginController) controller).setStage(stage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}
