module com.itmd.issuetrackingapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    requires java.sql;
    requires mysql.connector.java;

    opens com.itmd510.issuetrackingapplication.controllers;

    exports com.itmd510.issuetrackingapplication.models;
    exports com.itmd510.issuetrackingapplication.DB;

    opens com.itmd510.issuetrackingapplication to javafx.fxml;

    exports com.itmd510.issuetrackingapplication;
}