package com.itmd510.issuetrackingapplication.controllers;

import com.itmd510.issuetrackingapplication.config.IssueUpdateEvent;
import com.itmd510.issuetrackingapplication.config.SessionManager;
import com.itmd510.issuetrackingapplication.models.Issue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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
        Issue updatedIssue = new Issue();
        updatedIssue.setIssueId(Integer.parseInt(getIssueId()));
        updatedIssue.setTitle(getTitle());
        updatedIssue.setDescription(getDescription());
        updatedIssue.setStatus(getStatus());

        String currentUser = SessionManager.getInstance().getCurrentUser();
        updatedIssue.setAssignee(currentUser);

        updatedIssue.updateIssue();

        alterButton.fireEvent(new IssueUpdateEvent());

        Stage primaryStage = (Stage) alterButton.getScene().getWindow();

        UserController userController = (UserController) primaryStage.getUserData();

        if (userController != null) {
            userController.refreshUI();
        } else {
            System.err.println("UserController is null. Cannot refresh UI.");
        }
        closeDetailsForm();
    }


    private void closeDetailsForm() {
        Stage stage = (Stage) alterButton.getScene().getWindow();

        stage.close();
    }


}
