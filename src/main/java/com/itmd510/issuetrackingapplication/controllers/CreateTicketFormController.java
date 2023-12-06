package com.itmd510.issuetrackingapplication.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.itmd510.issuetrackingapplication.DB.DBConnector;
import com.itmd510.issuetrackingapplication.config.ConfigLoader;
import com.itmd510.issuetrackingapplication.config.SessionManager;
import com.itmd510.issuetrackingapplication.models.Issue;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CreateTicketFormController {

    @FXML
    private TextField titleField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField statusField;

    @FXML
    private TextField userNameField;

    public void initialize() {
        // Set default values and disable the fields
        statusField.setText("To Do");
        statusField.setDisable(true);

        // Set the currently logged-in user's username
        // Assuming you have a way to access the currently logged-in user's username
        userNameField.setText(SessionManager.getInstance().getLoggedInUsername());
        userNameField.setDisable(true);
    }

    @FXML
    private void handleSubmit() {
        // Retrieve values from the form fields
        String title = titleField.getText();
        String description = descriptionField.getText();
        String status = statusField.getText(); // Since it's disabled, you might consider setting a default value
        String assignee = userNameField.getText(); // Assuming the assignee is the current user

        // Retrieve the current user's user_id from the database
        int userId = getUserIdByUserName(assignee);

        if (userId != -1) {
            // Create a new Timestamp for the current time
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

            // Create a new Issue instance
            Issue newIssue = new Issue();
            newIssue.setTitle(title);
            newIssue.setDescription(description);
            newIssue.setStatus(status);
            newIssue.setAssignee(assignee);
            newIssue.setCreatedAt(currentTimestamp);
            newIssue.setUserId(userId);
            newIssue.setUserName(assignee);

            // Print the values before storing
            System.out.println("Title: " + newIssue.getTitle());
            System.out.println("Description: " + newIssue.getDescription());
            System.out.println("Status: " + newIssue.getStatus());
            System.out.println("Assignee: " + newIssue.getAssignee());
            System.out.println("Created At: " + newIssue.getCreatedAt());
            System.out.println("User ID: " + newIssue.getUserId());
            System.out.println("User Name: " + newIssue.getUserName());

            // Add the issue to the database
            newIssue.addIssue();

            // Optionally, you can close the form or perform other actions after submission
            // You might use a popup, dialog, or transition to show/hide the form
        } else {
            System.out.println("User not found. Please specify a valid user.");
        }
    }

    private int getUserIdByUserName(String userName) {
        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
                PreparedStatement preparedStatement = connection
                        .prepareStatement("SELECT userId FROM users WHERE username = ?")) {

            preparedStatement.setString(1, userName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("userId");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 or handle it appropriately if the user_id is not found
    }

}
