package com.itmd510.issuetrackingapplication.controllers;

import com.itmd510.issuetrackingapplication.models.Issue;
import com.itmd510.issuetrackingapplication.services.UserService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Timestamp;
import java.util.List;

public class IssueTrackingController {

    @FXML
    private TableView<Issue> issueTable;

    @FXML
    private TableColumn<Issue, Integer> idColumn;

    @FXML
    private TableColumn<Issue, String> titleColumn;

    @FXML
    private TableColumn<Issue, String> descriptionColumn;

    @FXML
    private TableColumn<Issue, String> statusColumn;

    @FXML
    private TableColumn<Issue, Timestamp> createdAtColumn;

    @FXML
    private TableColumn<Issue, String> userNameColumn;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField statusField;

    @FXML
    private ComboBox<String> userComboBox;

    private final UserService userService = new UserService();

    public void initialize() {
        // Initialize the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("issueId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));

        // Populate the table with data
        updateTable();
        populateUserComboBox();
    }

    private void populateUserComboBox() {
        // Replace this with your logic to fetch user names from your data source
        List<String> userNames = userService.getUserNames(); // Replace userService with your actual service

        // Populate the ComboBox with user names
        userComboBox.getItems().addAll(userNames);
    }

    private void updateTable() {
        ObservableList<Issue> issues = Issue.getAllIssues();
        issueTable.setItems(issues);
    }

    @FXML
    private void handleUpdateIssue() {
        Issue selectedIssue = issueTable.getSelectionModel().getSelectedItem();

        if (selectedIssue != null) {
            String title = titleField.getText();
            String description = descriptionArea.getText();
            String status = statusField.getText();

            if (!title.isEmpty() && !description.isEmpty() && !status.isEmpty()) {
                selectedIssue.setTitle(title);
                selectedIssue.setDescription(description);
                selectedIssue.setStatus(status);

                selectedIssue.updateIssue();
                updateTable(); // Refresh the table
                showAlert("Issue updated successfully!");
            } else {
                showAlert("All fields must be filled!");
            }
        } else {
            showAlert("Select an issue to update!");
        }
    }

    @FXML
    public void handleAddIssue() {
        String title = titleField.getText();
        String description = descriptionArea.getText();
        String status = statusField.getText();
        String selectedUserName = userComboBox.getValue();

        if (!title.isEmpty() && !description.isEmpty() && !status.isEmpty() && selectedUserName != null) {
            Issue newIssue = new Issue();
            newIssue.setTitle(title);
            newIssue.setDescription(description);
            newIssue.setStatus(status);
            newIssue.setUserName(selectedUserName);

            try {
                newIssue.addIssue(); // Add to the database
                updateTable(); // Refresh the table
                clearFields();
                showAlert("Issue added successfully!");
            } catch (Exception e) {
                showAlert("Error adding the issue. Please try again.");
                e.printStackTrace(); // Log the exception
            }
        } else {
            showAlert("All fields must be filled!");
        }
    }

    @FXML
    private void handleDeleteIssue() {
        Issue selectedIssue = issueTable.getSelectionModel().getSelectedItem();

        if (selectedIssue != null) {
            selectedIssue.deleteIssue();
            updateTable(); // Refresh the table
            showAlert("Issue deleted successfully!");
        } else {
            showAlert("Select an issue to delete!");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        titleField.clear();
        descriptionArea.clear();
        statusField.clear();
    }
}
