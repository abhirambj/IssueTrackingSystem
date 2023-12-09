package com.itmd510.issuetrackingapplication.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public abstract class BaseController {
    private Stage stage;
    private LoginController loginController;

    protected Stage getStage() {
        return stage;
    }

    protected void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected void navigateToLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itmd510/issuetrackingapplication/views/LoginView.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Login View");
            loginStage.setScene(new Scene(root));

            // Set the stage reference in the LoginController
            LoginController loginController = loader.getController();
            loginController.setStage(loginStage);

            // Close the current registration view
            closeStage();

            // Show the Login stage
            loginStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void closeStage() {
        Stage currentStage = getStage();
        if (currentStage != null) {
            currentStage.close();
        }
    }

    // Add the validateInput method
    protected boolean validateInput(String... inputs) {
        for (String input : inputs) {
            if (input == null || input.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}
