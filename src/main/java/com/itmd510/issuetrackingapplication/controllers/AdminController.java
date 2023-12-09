package com.itmd510.issuetrackingapplication.controllers;

import com.itmd510.issuetrackingapplication.config.SessionManager;
import com.itmd510.issuetrackingapplication.models.Issue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class AdminController extends BaseController {

    public Button refreshButton;
    private BaseController parentController;

    public VBox todoPaneContent;
    public VBox inProgressPaneContent;
    public VBox donePaneContent;
    @FXML
    private Button manageUsersButton;

    @FXML
    private Button manageProjectsButton;

    @FXML
    private Button generateReportsButton;

    @FXML
    private Button logoutButton; // Added logout button

    private String userRole; // New field for storing the user's role

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
    private SessionManager sessionManager;

    private List<Issue> adminIssues;

    @FXML
    private Button createButton;

    @FXML
    private TitledPane todoPane;

    @FXML
    private TitledPane inProgressPane;

    @FXML
    private TitledPane donePane;

    @FXML
    private ListView<VBox> todoListView;

    @FXML
    private ListView<VBox> inProgressListView;

    @FXML
    private ListView<VBox> doneListView;


    public void setStage(Stage stage) {
    }

    public void initialize() {
        fetchAdminIssues();
        populateUI();
    }

    @FXML
    private void handleManageUsers() {
        try {
            // Load the ManageUsers.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itmd510/issuetrackingapplication/views/ManageUsersView.fxml"));
            Parent manageUsersParent = loader.load();

            // Create a new stage for the ManageUsers.fxml
            Stage manageUsersStage = new Stage();
            manageUsersStage.initModality(Modality.APPLICATION_MODAL);
            manageUsersStage.setTitle("Manage Users");

            // Set the new content for the ManageUsers stage
            Scene manageUsersScene = new Scene(manageUsersParent);
            manageUsersStage.setScene(manageUsersScene);

            // Show the ManageUsers stage
            manageUsersStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an alert)
        }
    }

    private void clearPaneContent(TitledPane pane) {
        if (pane != null) {
            Node contentNode = pane.getContent();
            if (contentNode instanceof VBox) {
                ((VBox) contentNode).getChildren().clear();
            } else {
                System.err.println("Content of TitledPane is not a VBox.");
            }
        } else {
            System.err.println("TitledPane is null. Cannot clear content.");
        }
    }

    private void addToPane(TitledPane pane, Node content) {
        if (pane != null) {
            Node existingContent = pane.getContent();
            VBox vbox;

            if (existingContent instanceof VBox) {
                vbox = (VBox) existingContent;
            } else {
                vbox = new VBox();
                pane.setContent(vbox);
            }

            // Add the new content (custom issue card) to the existing VBox
            vbox.getChildren().add(content);
        } else {
            System.err.println("TitledPane is null. Cannot add content.");
        }
    }

    @FXML
    private void handleManageProjects() {
        try {
            // Load the ManageProjects.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itmd510/issuetrackingapplication/views/ManageProjectsView.fxml"));
            Parent manageProjectsParent = loader.load();

            // Create a new stage for the ManageProjects.fxml
            Stage manageProjectsStage = new Stage();
            manageProjectsStage.initModality(Modality.APPLICATION_MODAL);
            manageProjectsStage.setTitle("Manage Projects");

            // Set the new content for the ManageProjects stage
            Scene manageProjectsScene = new Scene(manageProjectsParent);
            manageProjectsStage.setScene(manageProjectsScene);

            // Get the controller and set the reference to the parent controller
            ManageProjectsController manageProjectsController = loader.getController();
            manageProjectsController.setParentController(this);

            // Show the ManageProjects stage
            manageProjectsStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an alert)
        }
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void populateUI() {
        // Clear existing content
        todoListView.getItems().clear();
        inProgressListView.getItems().clear();
        doneListView.getItems().clear();

        for (Issue issue : adminIssues) {
            // Load the custom issue card FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itmd510/issuetrackingapplication/views/IssueCard.fxml"));
            VBox card = null;
            try {
                card = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Access the controller of the custom card
            IssueCardController cardController = loader.getController();

            // Set data in the card
            cardController.setIssueIdText(issue.getIssueId());
            cardController.setTitle(issue.getTitle());
            cardController.setDescription(issue.getDescription());
            cardController.setStatus(issue.getStatus());

            // Organize cards based on status
            switch (issue.getStatus()) {
                case "To Do":
                    todoListView.getItems().add(card);
                    break;
                case "InProgress":
                    inProgressListView.getItems().add(card);
                    break;
                case "Done":
                    doneListView.getItems().add(card);
                    break;
            }
        }
    }


    @FXML
    private void handleLogout() {
        // Show a confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to log out?");
        alert.setContentText("Click OK to confirm, or Cancel to go back.");

        // Handle the user's choice
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // User clicked OK, proceed with logout
                SessionManager.getInstance().logout();

                try {
                    // Load the login view
                    Parent loginViewParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/itmd510/issuetrackingapplication/views/LoginView.fxml")));

                    // Get the reference to the current window (Stage)
                    Stage primaryStage = (Stage) logoutButton.getScene().getWindow();

                    // Set a new scene with a BorderPane as the root
                    BorderPane root = new BorderPane();
                    root.setCenter(loginViewParent);
                    Scene loginScene = new Scene(root);

                    // Set the new scene to the primaryStage
                    primaryStage.setScene(loginScene);
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle the exception (e.g., show an alert)
                }
            }
            // If response is not OK, do nothing (user canceled)
        });
    }

    @FXML
    private void handleCreateTicket() throws IOException {
        // Load the form from the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itmd510/issuetrackingapplication/views/CreateTicketForm.fxml"));
        Parent form = loader.load();

        // Create a new stage for the form
        Stage formStage = new Stage();
        formStage.initModality(Modality.APPLICATION_MODAL);
        formStage.setTitle("Create Issue");

        // Set the new content for the form stage
        Scene scene = new Scene(form);
        formStage.setScene(scene);

        // Set the controller for the form
        CreateTicketFormController formController = loader.getController();
        formController.setParentController(this); // Pass the reference to the UserController
        formController.setFormStage(formStage); // Pass the reference to the form stage

        // Show the form
        formStage.showAndWait(); // Use showAndWait to wait for the form to close before continuing

        // After the form is closed, refresh the UI in UserController
        refreshUI();
    }

    public void refreshUI() {
        fetchAdminIssues();
        populateUI();
    }

    @FXML
    private void handleRefresh() {
        refreshUI();
    }

    private void fetchAdminIssues() {
        // Fetch issues for the current user from the database
        adminIssues = Issue.getAllIssues();
        System.out.println(adminIssues);
    }

    public void setLoginController(LoginController loginController) {
    }

    public void handleCardPressed(MouseEvent mouseEvent) {
    }
}