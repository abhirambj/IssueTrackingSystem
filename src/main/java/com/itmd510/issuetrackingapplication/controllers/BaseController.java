package com.itmd510.issuetrackingapplication.controllers;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

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

    protected void closeStage() {
        Stage currentStage = getStage();
        if (currentStage != null) {
            currentStage.close();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}
