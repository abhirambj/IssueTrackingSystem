package com.itmd510.issuetrackingapplication.controllers;

import com.itmd510.issuetrackingapplication.config.SessionManager;
import com.itmd510.issuetrackingapplication.models.Issue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private Button logoutButton;

    private String userRole;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itmd510/issuetrackingapplication/views/ManageUsersView.fxml"));
            Parent manageUsersParent = loader.load();

            Stage manageUsersStage = new Stage();
            manageUsersStage.initModality(Modality.APPLICATION_MODAL);
            manageUsersStage.setTitle("Manage Users");

            Scene manageUsersScene = new Scene(manageUsersParent);
            manageUsersStage.setScene(manageUsersScene);

            manageUsersStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading Manage Users view. Please try again.");
        }
    }

    @FXML
    private void handleManageProjects() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itmd510/issuetrackingapplication/views/ManageProjectsView.fxml"));
            Parent manageProjectsParent = loader.load();

            Stage manageProjectsStage = new Stage();
            manageProjectsStage.initModality(Modality.APPLICATION_MODAL);
            manageProjectsStage.setTitle("Manage Projects");

            Scene manageProjectsScene = new Scene(manageProjectsParent);
            manageProjectsStage.setScene(manageProjectsScene);

            ManageProjectsController manageProjectsController = loader.getController();
            manageProjectsController.setParentController(this);

            manageProjectsStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading Manage Projects view. Please try again.");
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
        todoListView.getItems().clear();
        inProgressListView.getItems().clear();
        doneListView.getItems().clear();

        for (Issue issue : adminIssues) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itmd510/issuetrackingapplication/views/IssueCard.fxml"));
            VBox card = null;
            try {
                card = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            IssueCardController cardController = loader.getController();

            cardController.setIssueIdText(issue.getIssueId());
            cardController.setTitle(issue.getTitle());
            cardController.setDescription(issue.getDescription());
            cardController.setStatus(issue.getStatus());

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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to log out?");
        alert.setContentText("Click OK to confirm, or Cancel to go back.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                SessionManager.getInstance().logout();

                try {
                    Parent loginViewParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/itmd510/issuetrackingapplication/views/LoginView.fxml")));

                    Stage primaryStage = (Stage) logoutButton.getScene().getWindow();

                    BorderPane root = new BorderPane();
                    root.setCenter(loginViewParent);
                    Scene loginScene = new Scene(root);

                    primaryStage.setScene(loginScene);
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Error loading login view. Please try again.");
                }
            }
        });
    }

    @FXML
    private void handleCreateTicket() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itmd510/issuetrackingapplication/views/CreateTicketForm.fxml"));
            Parent form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Create Issue");

            Scene scene = new Scene(form);
            formStage.setScene(scene);

            CreateTicketFormController formController = loader.getController();
            formController.setParentController(this);
            formController.setFormStage(formStage);

            formStage.showAndWait();
            refreshUI();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading Create Ticket form. Please try again.");
        }
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
        adminIssues = Issue.getAllIssues();
    }

    public void setLoginController(LoginController loginController) {
    }

}