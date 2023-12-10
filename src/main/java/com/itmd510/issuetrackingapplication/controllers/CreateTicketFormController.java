package com.itmd510.issuetrackingapplication.controllers;

import com.itmd510.issuetrackingapplication.DB.DBConnector;
import com.itmd510.issuetrackingapplication.config.ConfigLoader;
import com.itmd510.issuetrackingapplication.config.SessionManager;
import com.itmd510.issuetrackingapplication.models.Issue;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;

public class CreateTicketFormController {

    @FXML
    public TextField titleField;

    @FXML
    public TextField descriptionField;
    @FXML
    public AnchorPane createTicketForm;
    @FXML
    public TextField managerField;

    @FXML
    private TextField statusField;

    @FXML
    private TextField userNameField;

    @FXML
    private Button submitButton; // Added reference to the submit button

    private BaseController parentController;

    private Stage formStage; // Reference to the form stage


    public void setParentController(BaseController parentController) {
        this.parentController = Objects.requireNonNull(parentController, "parentController must not be null");
    }

    @FXML
    private void handleCreateTicket() {
        // Existing code

        // Notify the parent controller to refresh UI
        if (parentController != null) {
            if (parentController instanceof UserController) {
                ((UserController) parentController).refreshUI();
            } else if (parentController instanceof ManagerController) {
                ((ManagerController) parentController).refreshUI();
            }
        }

        // Close the form
        Stage stage = (Stage) formStage.getScene().getWindow();
        stage.close();
    }

    public void setFormStage(Stage formStage) {
        this.formStage = formStage;
    }

    public void initialize() {
        // Set default values and disable the fields
        statusField.setText("To Do");
        statusField.setDisable(true);
        userNameField.setText(SessionManager.getInstance().getLoggedInUsername());
        userNameField.setDisable(true);

        // Bind the disable property of the submit button to a BooleanBinding
        BooleanBinding disableSubmitButton = Bindings.createBooleanBinding(
                () -> titleField.getText().isEmpty() || descriptionField.getText().isEmpty(),
                titleField.textProperty(),
                descriptionField.textProperty()
        );

        submitButton.disableProperty().bind(disableSubmitButton);

        String managerId = getManagerNameForCurrentUser();
        managerField.setText(managerId);
        managerField.setDisable(true);
    }

    @FXML
    private void handleSubmit() {
        // Retrieve values from the form fields
        String title = titleField.getText();
        String description = descriptionField.getText();
        String status = statusField.getText(); // Since it's disabled, you might consider setting a default value
        String assignee = userNameField.getText(); // Assuming the assignee is the current user
        String reportsTo = getManagerNameForCurrentUser();

        // Retrieve the current user's user_id from the database
        int userId = getUserIdByUserName(assignee);
        System.out.println(userId);

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
            newIssue.setReportsTo(reportsTo);
            newIssue.addIssue();
            clearFields();
            formStage.close();
        } else {
            System.out.println("User not found. Please specify a valid user.");
        }
    }

    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
    }

    private String getManagerNameForCurrentUser() {
        String currentUser = SessionManager.getInstance().getLoggedInUsername();

        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT u1.username AS managerName " +
                             "FROM its_users u1 " +
                             "JOIN its_users u2 ON u1.userId = u2.managerId " +
                             "WHERE u2.username = ?")) {

            preparedStatement.setString(1, currentUser);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("managerName");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    private int getUserIdByUserName(String userName) {
        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT userId FROM its_users WHERE username = ?")) {

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