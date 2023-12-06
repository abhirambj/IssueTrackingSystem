package com.itmd510.issuetrackingapplication.services;

import com.itmd510.issuetrackingapplication.DB.DBConnector;
import com.itmd510.issuetrackingapplication.config.ConfigLoader;
import com.itmd510.issuetrackingapplication.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    public List<String> getUserNames() {
        List<String> userNames = new ArrayList<>();

        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(), ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT username FROM users");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                userNames.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userNames;
    }


    public void updateUser(User user) {
        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(), ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET password = ? WHERE username = ?")) {

            preparedStatement.setString(1, user.getPassword());
            preparedStatement.setString(2, user.getUsername());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Check if a username is unique, excluding the current user's username
    public boolean isUsernameUnique(String newUsername, String currentUsername) {
        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(), ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) as count FROM users WHERE username = ? AND username <> ?")) {

            preparedStatement.setString(1, newUsername);
            preparedStatement.setString(2, currentUsername);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count == 0;
                }
            }
        } catch (SQLException e) {
            // Log the exception instead of printing the stack trace
            e.printStackTrace();  // You might replace this with a logging framework like SLF4J or java.util.logging
        }
        return false;
    }
    public User getUserByUsername(String username) {
        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(), ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ?")) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Create and return a User object based on the result set
                    return new User(
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("role_id")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserRoleByUsername(String username) {
        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(), ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT roles.role_name FROM users INNER JOIN roles ON users.role_id = roles.role_id WHERE username = ?")) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("role_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
