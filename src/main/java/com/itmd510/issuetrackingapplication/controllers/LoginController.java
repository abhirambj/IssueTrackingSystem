package com.itmd510.issuetrackingapplication.controllers;

import com.itmd510.issuetrackingapplication.config.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController extends BaseController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signInButton;

    private SessionManager sessionManager;

    @FXML
    private void handleSignUp() {
        openRegistrationView();
    }

    private void openRegistrationView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/itmd510/issuetrackingapplication/views/RegisterView.fxml"));
            Parent root = loader.load();

            // Get the controller for the registration view
            RegisterController registerController = loader.getController();

            // Set the login controller reference in the registration controller
            registerController.setLoginController(this);

            // Set the stage reference in the registration controller
            registerController.setStage(getStage());

            // Show the registration stage
            getStage().setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignIn(ActionEvent event) {
        if (sessionManager.isLoggedIn()) {
            showAlert("A user is already logged in.");
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (validateInput(username, password)) {
            String role = authenticateUser(username, password);

            if (role != null) {
                sessionManager.login(username);
                openUserSpecificView(role);
            } else {
                showAlert("Invalid username or password.");
            }
        } else {
            showAlert("Username and password must not be empty.");
        }
    }

    private boolean validateInput(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }

    private String authenticateUser(String username, String password) {
        // Replace this with your actual authentication logic, fetching the role from
        // the database
        return "user"; // Placeholder role, replace with actual logic
    }

    private void openUserSpecificView(String role) {
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root;

            switch (role) {
                case "admin":
                    loader.setLocation(
                            getClass().getResource("/com/itmd510/issuetrackingapplication/views/AdminView.fxml"));
                    root = loader.load();
                    AdminController adminController = loader.getController();
                    adminController.setLoginController(this);
                    adminController.setStage(getStage());
                    break;

                case "manager":
                    loader.setLocation(
                            getClass().getResource("/com/itmd510/issuetrackingapplication/views/ManagerView.fxml"));
                    root = loader.load();
                    ManagerController managerController = loader.getController();
                    managerController.setLoginController(this);
                    managerController.setStage(getStage());
                    break;

                case "user":
                    loader.setLocation(
                            getClass().getResource("/com/itmd510/issuetrackingapplication/views/UserView.fxml"));
                    root = loader.load();
                    UserController userController = loader.getController();
                    userController.setLoginController(this);
                    userController.setStage(getStage());
                    break;

                default:
                    showAlert("Invalid role for navigation.");
                    return;
            }

            getStage().getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
}
