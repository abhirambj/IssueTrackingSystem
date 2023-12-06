package com.itmd510.issuetrackingapplication;

import com.itmd510.issuetrackingapplication.config.SessionManager;
import com.itmd510.issuetrackingapplication.controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class IssueTrackingApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SessionManager sessionManager = SessionManager.getInstance();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/itmd510/issuetrackingapplication/views/LoginView.fxml"));
        Parent root = loader.load();

        LoginController loginController = loader.getController();
        loginController.setStage(primaryStage);
        loginController.setSessionManager(sessionManager);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Issue Tracking Application");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
