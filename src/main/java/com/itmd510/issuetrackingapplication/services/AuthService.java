package com.itmd510.issuetrackingapplication.services;

import com.itmd510.issuetrackingapplication.config.ConfigLoader;
import com.itmd510.issuetrackingapplication.models.User;
import com.itmd510.issuetrackingapplication.DB.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    public static User authenticateUser(String username, String password) {
        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(), ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM users WHERE username = ? AND password = ?")) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new User(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Authentication failed
    }
}
