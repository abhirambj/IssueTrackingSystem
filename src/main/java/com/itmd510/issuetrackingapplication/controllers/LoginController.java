package com.itmd510.issuetrackingapplication.controllers;

import com.itmd510.issuetrackingapplication.DB.DBConnector;
import com.itmd510.issuetrackingapplication.config.ConfigLoader;
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
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController extends BaseController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signInButton;

    private SessionManager sessionManager;

    @FXML
    public void initialize() {
        sessionManager = SessionManager.getInstance();
    }

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
        if (sessionManager == null) {
            showAlert("SessionManager is not initialized.");
            return;
        }

        if (sessionManager.isLoggedIn()) {
            showAlert("A user is already logged in.");
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (validateInput(username, password)) {
            String role = authenticateUser(username, password);

            System.out.println(role);
            if (role != null) {
                sessionManager.login(username);

                // Set the stage for LoginController before calling openUserSpecificView
                setStage((Stage) ((Node) event.getSource()).getScene().getWindow());

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
        // Hash the password before comparing with the database
        String hashedPassword = hashPassword(password);

        try (Connection connection = DBConnector.getConnection(
                ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(),
                ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT roleId FROM users WHERE username = ? AND password = ?")) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String roleId = resultSet.getString("roleId");

                    // Fetch the role_name using roleId from the roles table
                    String roleName = fetchRoleName(roleId);

                    return roleName;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the SQL exception appropriately, e.g., log or throw a custom exception
        }

        return null; // Return null if authentication fails
    }

    private String fetchRoleName(String roleId) {
        try (Connection connection = DBConnector.getConnection(
                ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(),
                ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT role_name FROM roles WHERE role_id = ?")) {

            preparedStatement.setString(1, roleId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Return the role_name
                    return resultSet.getString("role_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the SQL exception appropriately, e.g., log or throw a custom exception
        }

        return null; // Return null if roleId is not found or an error occurs
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] byteData = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte b : byteData) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
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