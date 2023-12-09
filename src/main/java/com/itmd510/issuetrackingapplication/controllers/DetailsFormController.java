package com.itmd510.issuetrackingapplication.controllers;

import com.itmd510.issuetrackingapplication.config.IssueUpdateEvent;
import com.itmd510.issuetrackingapplication.config.SessionManager;
import com.itmd510.issuetrackingapplication.models.Issue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DetailsFormController {
    @FXML
    public AnchorPane detailsForm;

    @FXML
    private TextField idField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField statusField;

    @FXML
    private Button alterButton;
    private int issueId;

    public void initialize() {
        // You can perform initialization here if needed
    }

    public void setTitle(String title) {
        titleField.setText(title);
    }

    public String getTitle() {
        return titleField.getText();
    }

    public void setDescription(String description) {
        descriptionField.setText(description);
    }

    public String getDescription() {
        return descriptionField.getText();
    }

    public void setStatus(String status) {
        statusField.setText(status);
    }

    public String getStatus() {
        return statusField.getText();
    }

    public String getIssueId() {
        return idField.getText();
    }

    public void setIssueId(String issueId) {
        idField.setText(issueId);
    }


    @FXML
    public void handleAlterButton() {
        // Create an Issue object with the new values
        Issue updatedIssue = new Issue();
        updatedIssue.setIssueId(Integer.parseInt(getIssueId()));
        updatedIssue.setTitle(getTitle());
        updatedIssue.setDescription(getDescription());
        updatedIssue.setStatus(getStatus());

        String currentUser = SessionManager.getInstance().getCurrentUser();
        updatedIssue.setAssignee(currentUser);

        updatedIssue.updateIssue();

        // Fire the event
        alterButton.fireEvent(new IssueUpdateEvent());
        System.out.println("Issue updated!");
        System.out.println("Firing IssueUpdateEvent");

        // Get the reference to the current window (Stage)
        Stage primaryStage = (Stage) alterButton.getScene().getWindow();

        // Get the UserController instance from userData
        UserController userController = (UserController) primaryStage.getUserData();

        // Null check
        if (userController != null) {
            // Refresh the UI in UserController
            userController.refreshUI();
        } else {
            System.err.println("UserController is null. Cannot refresh UI.");
        }
        // Close the current window
        closeDetailsForm();
    }


    private void closeDetailsForm() {
        // Get the reference to the current window (Stage)
        Stage stage = (Stage) alterButton.getScene().getWindow();

        // Close the current window
        stage.close();
    }


}
