package com.itmd510.issuetrackingapplication.controllers;

import com.itmd510.issuetrackingapplication.models.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Optional;

public class ManageProjectsController {

    private BaseController parentController;

    @FXML
    private TableView<Project> projectsTableView;

    @FXML
    private TableColumn<Project, Integer> projectIdColumn;

    @FXML
    private TableColumn<Project, String> projectNameColumn;

    @FXML
    private TableColumn<Project, String> clientNameColumn;

    @FXML
    private TableColumn<Project, String> projectLeadColumn;

    @FXML
    private TextField projectNameField;

    @FXML
    private TextField clientNameField;

    @FXML
    private TextField projectLeadField;

    @FXML
    private Button addProjectButton;

    @FXML
    private Button updateProjectButton;

    @FXML
    private Button deleteProjectButton;

    private ObservableList<Project> projectsList;

    @FXML
    private void initialize() {
        // Initialize the columns with the appropriate property values
        projectIdColumn.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        projectLeadColumn.setCellValueFactory(new PropertyValueFactory<>("projectLead"));

        // Create an empty list for the TableView
        projectsList = FXCollections.observableArrayList();
        projectsTableView.setItems(projectsList);

        // Load initial data into the TableView
        refreshProjectTableView();
    }

    @FXML
    private void handleAddProject() {
        // Create the custom dialog
        Dialog<Project> dialog = new Dialog<>();
        dialog.setTitle("Add Project");
        dialog.setHeaderText("Enter Project Details");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create and configure the form fields
        TextField projectNameInput = new TextField();
        projectNameInput.setPromptText("Project Name");
        TextField clientNameInput = new TextField();
        clientNameInput.setPromptText("Client Name");
        TextField projectLeadInput = new TextField();
        projectLeadInput.setPromptText("Project Lead");

        // Add form fields to the dialog
        dialog.getDialogPane().setContent(new VBox(10, projectNameInput, clientNameInput, projectLeadInput));

        // Enable/Disable Add button depending on whether a project name was entered
        Node addProjectButton = dialog.getDialogPane().lookupButton(addButtonType);
        addProjectButton.setDisable(true);

        // Validation for empty fields
        projectNameInput.textProperty().addListener((observable, oldValue, newValue) ->
                addProjectButton.setDisable(newValue.trim().isEmpty()));

        // Set the result converter for the dialog
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Project newProject = new Project();
                newProject.setProjectName(projectNameInput.getText());
                newProject.setClientName(clientNameInput.getText());
                newProject.setProjectLead(projectLeadInput.getText());
                return newProject;
            }
            return null;
        });

        // Show the dialog and wait for the user's response
        Optional<Project> result = dialog.showAndWait();

        // Process the result if the user clicked Add
        result.ifPresent(newProject -> {
            // Add the new project to the database and refresh the TableView
            newProject.addProject();
            showAlert("Project added successfully");

            // Refresh the TableView
            refreshProjectTableView();
        });
    }

    @FXML
    private void handleUpdateProject() {
        // Check if a project is selected in the TableView
        Project selectedProject = projectsTableView.getSelectionModel().getSelectedItem();

        if (selectedProject == null) {
            showAlert("Please select a project to update");
            return;
        }

        // Create the custom dialog
        Dialog<Project> dialog = new Dialog<>();
        dialog.setTitle("Update Project");
        dialog.setHeaderText("Update Project Details");

        // Set the button types
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create and configure the form fields
        TextField projectNameInput = new TextField();
        projectNameInput.setPromptText("Project Name");
        projectNameInput.setText(selectedProject.getProjectName()); // Set the current project name

        TextField clientNameInput = new TextField();
        clientNameInput.setPromptText("Client Name");
        clientNameInput.setText(selectedProject.getClientName()); // Set the current client name

        TextField projectLeadInput = new TextField();
        projectLeadInput.setPromptText("Project Lead");
        projectLeadInput.setText(selectedProject.getProjectLead()); // Set the current project lead

        // Add form fields to the dialog
        dialog.getDialogPane().setContent(new VBox(10, projectNameInput, clientNameInput, projectLeadInput));

        // Enable/Disable Update button depending on whether a project name was entered
        Node updateProjectButtonNode = dialog.getDialogPane().lookupButton(updateButtonType);
        updateProjectButtonNode.setDisable(true);

        // Validation for empty fields
        projectNameInput.textProperty().addListener((observable, oldValue, newValue) ->
                updateProjectButtonNode.setDisable(newValue.trim().isEmpty()));

        // Set the result converter for the dialog
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                // Update the selected project with the new values
                selectedProject.setProjectName(projectNameInput.getText());
                selectedProject.setClientName(clientNameInput.getText());
                selectedProject.setProjectLead(projectLeadInput.getText());
                return selectedProject;
            }
            return null;
        });

        // Show the dialog and wait for the user's response
        Optional<Project> result = dialog.showAndWait();

        // Process the result if the user clicked Update
        result.ifPresent(updatedProject -> {
            // Update the project in the database and refresh the TableView
            updatedProject.updateProject(); // Assuming there is an updateProject() method in the Project class
            showAlert("Project updated successfully");

            // Refresh the TableView
            refreshProjectTableView();
        });
    }

    @FXML
    private void handleDeleteProject() {
        // Check if a project is selected in the TableView
        Project selectedProject = projectsTableView.getSelectionModel().getSelectedItem();

        if (selectedProject == null) {
            showAlert("Please select a project to delete");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Delete Project");
        confirmationAlert.setContentText("Are you sure you want to delete the selected project?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User clicked OK, proceed with deletion
            selectedProject.deleteProject(); // Assuming there is a deleteProject() method in the Project class
            showAlert("Project deleted successfully");

            // Refresh the TableView
            refreshProjectTableView();
        }
    }

    public void setParentController(BaseController parentController) {
        this.parentController = Objects.requireNonNull(parentController, "parentController must not be null");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshProjectTableView() {
        // Clear the existing data and add all projects from the database
        projectsList.clear();
        projectsList.addAll(Project.getAllProjects());
    }
}
