package com.itmd510.issuetrackingapplication.controllers;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;

public class PasswordDialog extends Dialog<String> {

    public PasswordDialog() {
        setTitle("Password Dialog");
        setHeaderText("Enter Password");

        // Set the button types (OK and Cancel)
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create a GridPane layout for the dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Create a PasswordField for password input
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Add the PasswordField to the GridPane
        grid.add(passwordField, 0, 0);

        // Set the GridPane as the content of the dialog
        getDialogPane().setContent(grid);

        // Convert the result when the OK button is clicked
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return passwordField.getText();
            }
            return null;
        });
    }
}
