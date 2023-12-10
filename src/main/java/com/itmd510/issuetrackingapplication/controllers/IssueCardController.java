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

    @FXML
    private Button deleteButton;

    private UserController userController;
    public void setUserController(UserController userController) {
        this.userController = userController;
    }


    private ObservableList<Issue> issues;

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
        blend.setMode(BlendMode.SRC_ATOP);
        blend.setTopInput(colorInput);

        icon.setEffect(blend);
    }


    private void handleDelete() {
        int issueId = getIssueId();
        String title = titleText.getText();

        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Delete Issue");
        confirmationDialog.setContentText("Are you sure you want to delete issue '" + title + "'?");
        confirmationDialog.initModality(Modality.APPLICATION_MODAL);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Issue issueToDelete = new Issue();
                issueToDelete.setIssueId(issueId);
                issueToDelete.deleteIssue();

                if (issues != null) {
                    issues.removeIf(issue -> issue.getIssueId() == issueId);

                }

                refreshUI();
            }
        });
    }

    private void refreshUI() {
        if (userController != null) {
            userController.refreshUI();
        }
    }

    @FXML
    private void handleCardClicked(MouseEvent event) {
        if (event.getTarget() == deleteIcon) {
            return;
        }

        int issueId = Integer.parseInt(issueIdText.getText());
        String title = titleText.getText();
        String description = descriptionText.getText();
        String status = statusText.getText();

        openDetailsForm(issueId, title, description, status);
    }


    private void openDetailsForm(int issueId, String title, String description, String status) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/itmd510/issuetrackingapplication/views/DetailsForm.fxml"));
            Parent form = loader.load();

            DetailsFormController detailsController = loader.getController();

            detailsController.setIssueId(String.valueOf(issueId));
            detailsController.setTitle(title);
            detailsController.setDescription(description);
            detailsController.setStatus(status);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Issue Details");
            stage.setScene(new Scene(form));

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setIssueIdText(int id) {
        issueIdText.setText(String.valueOf(id));
    }

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
        switch (status.toLowerCase()) {
            case "to do", "inprogress", "done":
                setStatusTextColor("white");
                break;
            default:
                setStatusTextColor("black");
                break;
        }

        statusText.setText(status);
    }

    private void setStatusTextColor(String color) {
        statusText.setFill(Color.web(color));
    }
}
