package com.itmd510.issuetrackingapplication.controllers;

import com.itmd510.issuetrackingapplication.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ManageUsersController {

    @FXML
    private TableView<User> usersTableView;

    @FXML
    private TableColumn<User, Integer> userIdColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> passwordColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, Integer> managerIdColumn;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField roleField;

    @FXML
    private TextField emailField;

    @FXML
    private Button addUserButton;

    @FXML
    private Button deleteUserButton;

    private ObservableList<User> usersList;

    @FXML
    private void initialize() {
        // Initialize the columns with the appropriate property values
        userIdColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("password"));
        roleColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("roleId"));
        emailColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
        managerIdColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("managerId"));

        // Create an empty list for the TableView
        usersList = FXCollections.observableArrayList();
        usersTableView.setItems(usersList);

        // Load initial data into the TableView
        refreshUserTableView();
    }

    @FXML
    private void handleAddUser() {
        // Create a TextInputDialog for user input
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add User");
        dialog.setHeaderText("Enter User Information");
        dialog.setContentText("Username:");

        // Show the dialog and wait for the user's response
        dialog.showAndWait().ifPresent(username -> {
            // Now 'username' contains the entered username

            // Prompt for password
            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setTitle("Add User");
            passwordDialog.setHeaderText("Enter User Information");
            passwordDialog.setContentText("Password:");

            // Show the password dialog and wait for the user's response
            passwordDialog.showAndWait().ifPresent(password -> {
                // Now 'password' contains the entered password

                // Prompt for email
                TextInputDialog emailDialog = new TextInputDialog();
                emailDialog.setTitle("Add User");
                emailDialog.setHeaderText("Enter User Information");
                emailDialog.setContentText("Email:");

                // Show the email dialog and wait for the user's response
                emailDialog.showAndWait().ifPresent(email -> {
                    // Now 'email' contains the entered email

                    // Prompt for role
                    TextInputDialog roleDialog = new TextInputDialog();
                    roleDialog.setTitle("Add User");
                    roleDialog.setHeaderText("Enter User Information");
                    roleDialog.setContentText("Role:");

                    // Show the role dialog and wait for the user's response
                    roleDialog.showAndWait().ifPresent(role -> {
                        // Now 'role' contains the entered role

                        TextInputDialog managerIdDialog = new TextInputDialog();
                        managerIdDialog.setTitle("Add User");
                        managerIdDialog.setHeaderText("Enter User Information");
                        managerIdDialog.setContentText("Manager ID:");
                        managerIdDialog.showAndWait().ifPresent(managerId -> {

                        // Create a new User object with the entered information
                        User newUser = new User();

                        // Add the new user to the database and refresh the TableView
                        newUser.addUser(username, password, role, email, managerId);
                        showAlert("User added successfully");
                        refreshUserTableView();
                        });
                    });
                });
            });
        });
    }


    @FXML
    private Button updateUserButton;

    @FXML
    private void handleUpdateUser() {
        // Get the selected user from the TableView
        User selectedUser = usersTableView.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            // Create a TextInputDialog for user input
            TextInputDialog dialog = new TextInputDialog(selectedUser.getUsername());
            dialog.setTitle("Update User");
            dialog.setHeaderText("Enter Updated User Information");
            dialog.setContentText("Username:");

            // Show the dialog and wait for the user's response
            dialog.showAndWait().ifPresent(username -> {
                // Now 'username' contains the entered username

                // Prompt for email
                TextInputDialog emailDialog = new TextInputDialog(selectedUser.getEmail());
                emailDialog.setTitle("Update User");
                emailDialog.setHeaderText("Enter Updated User Information");
                emailDialog.setContentText("Email:");

                // Show the email dialog and wait for the user's response
                emailDialog.showAndWait().ifPresent(email -> {
                    // Now 'email' contains the entered email

                    // Prompt for role
                    TextInputDialog roleDialog = new TextInputDialog(selectedUser.getRoleId());
                    roleDialog.setTitle("Update User");
                    roleDialog.setHeaderText("Enter Updated User Information");
                    roleDialog.setContentText("Role:");

                    // Show the role dialog and wait for the user's response
                    roleDialog.showAndWait().ifPresent(role -> {
                        // Now 'role' contains the entered role

                        TextInputDialog managerIdDialog = new TextInputDialog(selectedUser.getManagerId());
                        managerIdDialog.setTitle("Update User");
                        managerIdDialog.setHeaderText("Enter Updated User Information");
                        managerIdDialog.setContentText("Manager ID:");

                        // Show the managerId dialog and wait for the user's response
                        managerIdDialog.showAndWait().ifPresent(managerId -> {
                            // Now 'managerId' contains the entered managerId

                            // Prompt for password update
                            PasswordDialog passwordDialog = new PasswordDialog();
                            passwordDialog.setTitle("Update User");
                            passwordDialog.setHeaderText("Enter New Password (Leave blank to keep the current password)");
                            passwordDialog.setContentText("Password:");

                            // Show the password dialog and wait for the user's response
                            passwordDialog.showAndWait().ifPresent(newPassword -> {
                                // Now 'newPassword' contains the entered new password

                                // Update the selected user in the database and refresh the TableView
                                selectedUser.setUsername(username);
                                selectedUser.setRoleId(role);
                                selectedUser.setEmail(email);
                                selectedUser.setManagerId(managerId);

                                // Update password only if a new password is entered
                                if (!newPassword.isEmpty()) {
                                    selectedUser.setPassword(hashPassword(newPassword));
                                }

                                selectedUser.updateUser();
                                showAlert("User updated successfully");
                                refreshUserTableView();
                            });
                        });
                    });
                });
            });
        } else {
            showAlert("Please select a user to update");
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] byteData = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte b : byteData) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void handleDeleteUser() {
        // Get the selected user from the TableView
        User selectedUser = usersTableView.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            // Delete the selected user from the database and refresh the TableView
            selectedUser.deleteUser();
            showAlert("User deleted successfully");
            refreshUserTableView();
        } else {
            showAlert("Please select a user to delete");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshUserTableView() {
        // Clear the existing data and add all users from the database
        usersList.clear();
        usersList.addAll(User.getAllUsers());
    }
}
