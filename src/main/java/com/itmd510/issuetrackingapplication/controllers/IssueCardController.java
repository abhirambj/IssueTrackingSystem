package com.itmd510.issuetrackingapplication.controllers;

import com.itmd510.issuetrackingapplication.models.Issue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class IssueCardController {

    @FXML
    private ImageView deleteIcon;

    private String userRole;

    @FXML
    private Text issueIdText;

    @FXML
    private Text titleText;

    @FXML
    private Text descriptionText;

    @FXML
    private Text statusText;

    // Assuming you have a delete button in your FXML
    @FXML
    private Button deleteButton;

    private UserController userController; // Add this field

    // Setter method for UserController reference
    public void setUserController(UserController userController) {
        this.userController = userController;
    }


    private ObservableList<Issue> issues; // Assuming you have a list of issues

    // Setter for the issues list
    public void setIssues(ObservableList<Issue> issues) {
        this.issues = issues;
    }

    @FXML
    public void initialize() {
        deleteIcon.setOnMouseClicked(event -> handleDelete());
        setIconColor(deleteIcon, 1.0, 1.0, 1.0);
    }

    private void setIconColor(ImageView icon, double red, double green, double blue) {
        ColorInput colorInput = new ColorInput(
                0, 0,
                icon.getImage().getWidth(),
                icon.getImage().getHeight(),
                javafx.scene.paint.Color.rgb((int) (255 * red), (int) (255 * green), (int) (255 * blue))
        );

        Blend blend = new Blend();
        blend.setMode(BlendMode.SRC_ATOP); // Use BlendMode from javafx.scene.effect.BlendMode
        blend.setTopInput(colorInput);

        icon.setEffect(blend);
    }


    private void handleDelete() {
        // Retrieve the data from the issue card
        int issueId = getIssueId();
        String title = titleText.getText();

        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Delete Issue");
        confirmationDialog.setContentText("Are you sure you want to delete issue '" + title + "'?");
        confirmationDialog.initModality(Modality.APPLICATION_MODAL);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Create an instance of the Issue class
                Issue issueToDelete = new Issue();
                // Set the necessary data for the issue
                issueToDelete.setIssueId(issueId);
                // Call the deleteIssue method to delete the issue
                issueToDelete.deleteIssue();

                // Refresh the UI by updating the issues list
                if (issues != null) {
                    // Remove the issue from the list
                    issues.removeIf(issue -> issue.getIssueId() == issueId);

                    // Optionally, update your UI elements here based on the modified issues list
                }

                // Refresh the UI in UserController
                refreshUI();
            }
        });
    }

    // Assuming you have a reference to the UserController in IssueCardController
    private void refreshUI() {
        // Check if the UserController reference is available
        if (userController != null) {
            // Call the refreshUI method in UserController
            userController.refreshUI();
        }
    }

    @FXML
    private void handleCardClicked(MouseEvent event) {
        // Check if the delete icon is clicked
        if (event.getTarget() == deleteIcon) {
            // Issue is being deleted, do not open the details form
            return;
        }

        // Retrieve the data from the clicked issue card
        int issueId = Integer.parseInt(issueIdText.getText());
        String title = titleText.getText();
        String description = descriptionText.getText();
        String status = statusText.getText();

        // Open the details form with the retrieved data
        openDetailsForm(issueId, title, description, status);
    }


    private void openDetailsForm(int issueId, String title, String description, String status) {
        try {
            // Load the details form from the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itmd510/issuetrackingapplication/views/DetailsForm.fxml"));
            Parent form = loader.load();

            // Access the controller of the details form
            DetailsFormController detailsController = loader.getController();

            // Set the data in the details form
            detailsController.setIssueId(String.valueOf(issueId));
            detailsController.setTitle(title);
            detailsController.setDescription(description);
            detailsController.setStatus(status);

            // Create a new stage (window) for the details form
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Issue Details");
            stage.setScene(new Scene(form));

            // Show the details form
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setIssueIdText(int id) {
        issueIdText.setText(String.valueOf(id));
    }

    // Getter method for issueId as int
    public int getIssueId() {
        return Integer.parseInt(issueIdText.getText());
    }

    public void setTitle(String title) {
        titleText.setText(title);
    }

    public void setDescription(String description) {
        descriptionText.setText(description);
    }

    public void setStatus(String status) {
        // Set text color based on status
        switch (status.toLowerCase()) {
            case "to do", "inprogress", "done":
                setStatusTextColor("white");
                break;
            default:
                // Default text color (black) for unknown status
                setStatusTextColor("black");
                break;
        }

        // Set status text
        statusText.setText(status);
    }

    private void setStatusTextColor(String color) {
        statusText.setFill(Color.web(color));
    }
}
