package com.itmd510.issuetrackingapplication.controllers;

import com.itmd510.issuetrackingapplication.config.IssueUpdateEvent;
import com.itmd510.issuetrackingapplication.config.SessionManager;
import com.itmd510.issuetrackingapplication.models.Issue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class UserController extends BaseController {

    @FXML
    public AnchorPane draggableCard;
    public VBox todoPaneContent;
    public VBox inProgressPaneContent;
    public VBox donePaneContent;

    @FXML
    private Button logoutButton;

    @FXML
    private Button createButton;

    @FXML
    private TitledPane todoPane;

    @FXML
    private TitledPane inProgressPane;

    @FXML
    private TitledPane donePane;

    @FXML
    private Button refreshButton;

    private SessionManager sessionManager;
    private List<Issue> userIssues;

    private Stage createTicketFormStage;  // Add a field to store the reference to the form stage

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        sessionManager = SessionManager.getInstance();
        initializeUI();
        fetchUserIssues();
        populateUI();
        createButton.addEventHandler(IssueUpdateEvent.ISSUE_UPDATE, event -> {
            fetchUserIssues();
            populateUI();
        });
    }

    @FXML
    private void handleRefresh() {
        refreshUI();
    }

    public static Parent loadFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(UserController.class.getResource("/com/itmd510/issuetrackingapplication/views/UserView.fxml"));
        return loader.load();
    }

    public UserController() {
    }

    private void initializeUI() {
        if (logoutButton == null) {
            logoutButton = new Button();

            // Set the action for the logoutButton
            logoutButton.setOnAction(event -> handleLogout());
        }
        logoutButton.setOnAction(event -> handleLogout());
    }

    private void fetchUserIssues() {
        // Fetch issues for the current user from the database
        userIssues = Issue.getIssuesForUser(sessionManager.getCurrentUser());
        System.out.println("User Issues: " + userIssues);
    }

    private void populateUI() {
        // Clear existing content
        clearPaneContent(todoPane);
        clearPaneContent(inProgressPane);
        clearPaneContent(donePane);

        for (Issue issue : userIssues) {
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

            // Set the reference to UserController
            cardController.setUserController(this);

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
                default:
                    System.out.println("Invalid status");
            }
        }
    }

    private void clearPaneContent(TitledPane pane) {
        if (pane != null) {
            Node contentNode = pane.getContent();
            if (contentNode instanceof VBox) {
                ((VBox) contentNode).getChildren().clear();
            } else {
                VBox vbox = new VBox();  // Create a new VBox
                pane.setContent(vbox);   // Set it as the content of the TitledPane
            }
        } else {
            System.err.println("TitledPane is null. Cannot clear content.");
        }
    }

    @FXML
    private void handleCardPressed() {
    }

    private void addToPane(TitledPane pane, Node content) {
        if (pane != null) {
            Node existingContent = pane.getContent();
            VBox vbox;

            if (existingContent instanceof VBox) {
                vbox = (VBox) existingContent;
            } else {
                vbox = new VBox();       // Create a new VBox
                pane.setContent(vbox);   // Set it as the content of the TitledPane
            }

            // Add the new content (custom issue card) to the existing VBox
            vbox.getChildren().add(content);
        } else {
            System.err.println("TitledPane is null. Cannot add content.");
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

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCreateTicket() throws IOException {
        if (createTicketFormStage == null) {
            // Only create a new stage if it doesn't exist yet
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itmd510/issuetrackingapplication/views/CreateTicketForm.fxml"));
            Parent form = loader.load();

            // Create a new stage for the form
            createTicketFormStage = new Stage();
            createTicketFormStage.initModality(Modality.APPLICATION_MODAL);
            createTicketFormStage.setTitle("Create Issue");

            // Set the controller for the form
            CreateTicketFormController formController = loader.getController();
            formController.setParentController(this); // Pass the reference to the UserController
            formController.setFormStage(createTicketFormStage); // Pass the reference to the form stage

            // Set the new content for the form stage
            Scene scene = new Scene(form);
            createTicketFormStage.setScene(scene);

            // Set a listener for the close request to reset form fields
            createTicketFormStage.setOnCloseRequest(event -> resetFormFields());
        }

        // Show the form
        createTicketFormStage.showAndWait(); // Use showAndWait to wait for the form to close before continuing

        // Refresh the UI in UserController
        refreshUI();
    }

    private void resetFormFields() {
        // Access the controller of the form using FXMLLoader
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itmd510/issuetrackingapplication/views/CreateTicketForm.fxml"));
        try {
            Parent form = loader.load();
            CreateTicketFormController formController = loader.getController();

            // Reset form fields
            formController.titleField.clear();
            formController.descriptionField.clear();
            // Clear other fields as needed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add a method to handle UI refresh
    public void refreshUI() {
        fetchUserIssues();
        populateUI();
    }
}