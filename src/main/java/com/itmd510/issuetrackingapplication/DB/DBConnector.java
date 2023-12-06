package com.itmd510.issuetrackingapplication.DB;

import com.itmd510.issuetrackingapplication.config.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error loading MySQL JDBC driver", e);
        }
    }

    public static Connection getConnection(String databaseUrl, String databaseUser, String databasePassword) throws SQLException {
        String url = ConfigLoader.getDatabaseUrl();
        String user = ConfigLoader.getDatabaseUser();
        String password = ConfigLoader.getDatabasePassword();
        return DriverManager.getConnection(url, user, password);
    }
}
