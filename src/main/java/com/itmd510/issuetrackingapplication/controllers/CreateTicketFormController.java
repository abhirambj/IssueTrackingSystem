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
    private Button submitButton;

    private BaseController parentController;

    private Stage formStage;


    public void setParentController(BaseController parentController) {
        this.parentController = Objects.requireNonNull(parentController, "parentController must not be null");
    }

    @FXML
    private void handleCreateTicket() {

        if (parentController != null) {
            if (parentController instanceof UserController) {
                ((UserController) parentController).refreshUI();
            } else if (parentController instanceof ManagerController) {
                ((ManagerController) parentController).refreshUI();
            }
        }

        Stage stage = (Stage) formStage.getScene().getWindow();
        stage.close();
    }

    public void setFormStage(Stage formStage) {
        this.formStage = formStage;
    }

    public void initialize() {
        statusField.setText("To Do");
        statusField.setDisable(true);
        userNameField.setText(SessionManager.getInstance().getLoggedInUsername());
        userNameField.setDisable(true);

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
        String title = titleField.getText();
        String description = descriptionField.getText();
        String status = statusField.getText();
        String assignee = userNameField.getText();
        String reportsTo = getManagerNameForCurrentUser();

        int userId = getUserIdByUserName(assignee);
        System.out.println(userId);

        if (userId != -1) {
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

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

        return -1;
    }

}