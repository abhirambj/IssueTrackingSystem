package com.itmd510.issuetrackingapplication.controllers;

import com.itmd510.issuetrackingapplication.config.SessionManager;
import com.itmd510.issuetrackingapplication.models.Issue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.List;

public class ManagerController extends BaseController {

    public Button createButton;
    public Button logoutButton;
    public TitledPane todoPane;
    public VBox todoPaneContent;
    public TitledPane inProgressPane;
    public VBox inProgressPaneContent;
    public TitledPane donePane;
    public VBox donePaneContent;
    public Button refreshButton;
    private Stage stage; // Reference to the stage
    private LoginController loginController;

    private List<Issue> managerIssues;

    private SessionManager sessionManager;

    // Setter method for the stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        // Initialize the session manager
        sessionManager = SessionManager.getInstance();

        if (logoutButton == null) {
            logoutButton = new Button();

            // Set the action for the logoutButton
            logoutButton.setOnAction(event -> handleLogout());
        }
        logoutButton.setOnAction(event -> handleLogout());

        // Fetch issues for the current user from the database
        fetchManagerIssues();

        // Populate the UI with the issues
        populateUI();
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
                    Parent loginViewParent = FXMLLoader.load(getClass().getResource("/com/itmd510/issuetrackingapplication/views/LoginView.fxml"));

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

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public void handleCardPressed(MouseEvent mouseEvent) {
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
        formController.setParentController(this);
        formController.setFormStage(formStage); // Pass the reference to the form stage

        // Show the form
        formStage.showAndWait(); // Use showAndWait to wait for the form to close before continuing

        // After the form is closed, refresh the UI in UserController
        refreshUI();
    }

    public void refreshUI() {
        fetchManagerIssues();
        populateUI();
    }

    private void fetchManagerIssues() {
        // Fetch issues for the current user from the database
        managerIssues = Issue.getIssuesForUser(sessionManager.getCurrentUser());
        System.out.println(managerIssues);
    }

    @FXML
    private void handleRefresh() {
        refreshUI();
    }

    private void populateUI() {
        // Clear existing content
        clearPaneContent(todoPane);
        clearPaneContent(inProgressPane);
        clearPaneContent(donePane);

        for (Issue issue : managerIssues) {
            // Load the custom issue card FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itmd510/issuetrackingapplication/views/IssueCard.fxml"));
            VBox card = null;
            try {
                card = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Check if the card is null
            if (card == null) {
                System.err.println("Error loading IssueCard.fxml");
                continue; // Skip to the next iteration if there's an error
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
                    addToPane(todoPane, card);
                    break;
                case "InProgress":
                    addToPane(inProgressPane, card);
                    break;
                case "Done":
                    addToPane(donePane, card);
                    break;
            }
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
                // Create a new VBox and set it as the content of the TitledPane
                vbox = new VBox();
                pane.setContent(vbox);
                System.out.println("Created a new VBox for TitledPane");
            }

            // Add the new content (custom issue card) to the existing VBox
            vbox.getChildren().add(content);
        } else {
            System.err.println("TitledPane is null. Cannot add content.");
        }
    }

}
