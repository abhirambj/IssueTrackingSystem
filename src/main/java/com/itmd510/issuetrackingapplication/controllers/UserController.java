package com.itmd510.issuetrackingapplication.controllers;

import com.itmd510.issuetrackingapplication.config.SessionManager;
import com.itmd510.issuetrackingapplication.models.Issue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserController extends BaseController {
    @FXML
    public AnchorPane draggableCard;

    @FXML
    private TextField searchField;

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
    private VBox contentContainer;



    private SessionManager sessionManager;
    private List<Issue> userIssues;

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public UserController() {
        this.sessionManager = SessionManager.getInstance();
        initializeUI();
        fetchUserIssues();
        populateUI();
    }

    private void initializeUI() {
        if (searchField == null) {
            searchField = new TextField();

            searchField.setOnAction(event -> searchIssues());
        }
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
        System.out.println(userIssues);
    }

    private void populateUI() {
        // Clear existing content
        clearPaneContent(todoPane);
        clearPaneContent(inProgressPane);
        clearPaneContent(donePane);

        for (Issue issue : userIssues) {
            Text issueText = new Text(
                    issue.getTitle() + "\n" + issue.getDescription() + "\nStatus: " + issue.getStatus());
            issueText.setFont(Font.font(14));
            System.out.println(issueText);

            // Organize issues based on status
            switch (issue.getStatus()) {
                case "Todo":
                    addToPane(todoPane, issueText);
                    break;
                case "InProgress":
                    addToPane(inProgressPane, issueText);
                    break;
                case "Done":
                    addToPane(donePane, issueText);
                    break;
            }
        }
    }

    private void clearPaneContent(TitledPane pane) {
        Node contentNode = pane.getContent();
        if (contentNode instanceof VBox) {
            ((VBox) contentNode).getChildren().clear();
        }
    }

    private void addToPane(TitledPane pane, Text issueText) {
        Node contentNode = pane.getContent();
        if (contentNode instanceof VBox) {
            ((VBox) contentNode).getChildren().add(issueText);
        }
    }

    @FXML
    private void searchIssues() {
        System.out.println("Search triggered!");
        String searchTerm = searchField.getText();

        // Implement your actual logic for searching issues
        // Here, we're using a static method in the Issue class as an example
        List<Issue> searchResults = Issue.searchIssues(searchTerm, sessionManager.getCurrentUser());

        // Update the UI based on the search results
        updateUIWithSearchResults(searchResults);
    }

    private void updateUIWithSearchResults(List<Issue> searchResults) {
        contentContainer.getChildren().clear();

        for (Issue issue : searchResults) {
            Text issueText = new Text(
                    issue.getTitle() + "\n" + issue.getDescription() + "\nStatus: " + issue.getStatus());
            issueText.setFont(Font.font(14));

            // Add the Text node to the content container
            contentContainer.getChildren().add(issueText);
        }
    }

    @FXML
    private void handleLogout() {
        // Implement logout logic
        // Redirect to the login view or perform other necessary actions
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
        // Load the form from the FXML file
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/itmd510/issuetrackingapplication/views/CreateTicketForm.fxml"));
        Parent form = loader.load();

        // Create a new stage (window) for the form
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Create Ticket");
        stage.setScene(new Scene(form));

        // Show the form
        stage.show();
    }
}
