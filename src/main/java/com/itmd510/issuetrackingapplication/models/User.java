package com.itmd510.issuetrackingapplication.models;

import com.itmd510.issuetrackingapplication.DB.DBConnector;
import com.itmd510.issuetrackingapplication.config.ConfigLoader;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String username;
    private String password;
    private String roleId;
    private String email;

    private String managerId;

    public User() {
        // Default constructor
    }

    public User(String username, String password, String roleId, String email) {
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.email = email;
    }

    public User(int userId, String username, String password, String roleId, String email, String managerId) {
        this.userId = String.valueOf(userId);
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.email = email;
        this.managerId = managerId;
    }

    public static List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();

        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int userId = resultSet.getInt("userId");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String roleId = resultSet.getString("roleId");
                String email = resultSet.getString("email");
                String managerId = resultSet.getString("managerId");

                User user = new User(userId, username, password, roleId, email, managerId);
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void deleteUser() {
        try (Connection connection = DBConnector.getConnection(
                ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(),
                ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM users WHERE userId = ?")) {
            preparedStatement.setInt(1, Integer.parseInt(userId));

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                // Deletion successful
                System.out.println("User deleted successfully");
            } else {
                // No rows affected, user not found
                System.out.println("User not found or deletion unsuccessful");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the SQL exception appropriately, e.g., log or throw a custom exception
        }
    }


    public void updateUser() {
        try (Connection connection = DBConnector.getConnection(
                ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(),
                ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE users SET username = ?, password = ?, roleId = (SELECT roleId FROM roles WHERE role_name = ?), email = ? WHERE userId = ?")) {
            preparedStatement.setString(1, username);
            String hashedPassword = hashPassword(password);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, roleId);
            preparedStatement.setString(4, email);
            preparedStatement.setInt(5, Integer.parseInt(userId));

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                // Update successful
                System.out.println("User updated successfully");
            } else {
                // No rows affected, user not found
                System.out.println("User not found or update unsuccessful");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the SQL exception appropriately, e.g., log or throw a custom exception
        }
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean addUser(String username, String password, String roleId, String email, String managerId) {
        try (Connection connection = DBConnector.getConnection(
                ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(),
                ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO users (username, password, roleId, email) VALUES (?, ?, (SELECT role_id FROM roles WHERE role_name = ? LIMIT 1), ?)")) {

            preparedStatement.setString(1, username);

            // Hash the password before setting it in the statement
            String hashedPassword = hashPassword(password);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, roleId); // Set the roleId in the subquery
            preparedStatement.setString(4, email);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // User record inserted successfully, now update the managerId
                try (PreparedStatement updateStatement = connection.prepareStatement(
                        "UPDATE users SET managerId = ? WHERE username = ?")) {
                    updateStatement.setString(1, managerId);
                    updateStatement.setString(2, username);
                    updateStatement.executeUpdate();
                }
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the SQL exception appropriately, e.g., log or throw a custom exception
            return false;
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


    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }
}
