package com.itmd510.issuetrackingapplication.controllers;

import com.itmd510.issuetrackingapplication.DB.DBConnector;
import com.itmd510.issuetrackingapplication.config.ConfigLoader;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterController extends BaseController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;

    private LoginController loginController;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();

        if (validateInput(username, password, email)) {
            String hashedPassword = hashPassword(password);

            if (hashedPassword != null && registerUser(username, hashedPassword, email)) {
                showAlert("Registration successful. You can now login.");
                closeStage();
                loginController.getStage().show();
            } else {
                showAlert("Registration failed. Please try again.");
            }
        } else {
            showAlert("Username, password, and email must not be empty.");
        }
    }

    private boolean validateInput(String username, String password, String email) {
        return !username.isEmpty() && !password.isEmpty() && !email.isEmpty();
    }

    private boolean registerUser(String username, String hashedPassword, String email) {
        try (Connection connection = DBConnector.getConnection(
                ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(),
                ConfigLoader.getDatabasePassword());
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO users (username, password, role_id, email) VALUES (?, ?, (SELECT role_id FROM roles WHERE role_name = 'User'), ?)")) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, email);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}
