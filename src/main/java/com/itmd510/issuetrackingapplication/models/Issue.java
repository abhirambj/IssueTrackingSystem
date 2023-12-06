package com.itmd510.issuetrackingapplication.models;

import com.itmd510.issuetrackingapplication.DB.DBConnector;
import com.itmd510.issuetrackingapplication.config.ConfigLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Issue {

    private int issue_id;
    private String title;
    private String description;
    private String status;
    private Timestamp createdAt;
    private int userId;
    private String userName;

    private String assignee;

    private String reportsTo;

    public Issue() {
        // Default constructor
    }

    public Issue(int issue_id, String title, String description, String status, Timestamp createdAt, int userId,
            String assignee, String reportsTo) {
        this.issue_id = issue_id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.userId = userId;
        this.reportsTo = reportsTo;
        this.assignee = assignee;
    }

    public static List<Issue> searchIssues(String searchTerm, String currentUser) {
        List<Issue> searchResults = new ArrayList<>();

        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM issues WHERE (title LIKE ? OR description LIKE ?) AND assignee = (SELECT userId FROM users WHERE username = ?)")) {

            preparedStatement.setString(1, "%" + searchTerm + "%");
            preparedStatement.setString(2, "%" + searchTerm + "%");
            preparedStatement.setString(3, currentUser);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Issue issue = new Issue();
                    issue.setIssueId(resultSet.getInt("issue_id"));
                    issue.setTitle(resultSet.getString("title"));
                    issue.setDescription(resultSet.getString("description"));
                    issue.setStatus(resultSet.getString("status"));
                    issue.setCreatedAt(resultSet.getTimestamp("created_at"));
                    issue.setUserName(getUserNameByUserId(connection, resultSet.getInt("userId")));
                    issue.setAssignee(getUserNameByUserId(connection, resultSet.getInt("assignee")));
                    searchResults.add(issue);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return searchResults;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getIssueId() {
        return issue_id;
    }

    public void setIssueId(int issue_id) {
        this.issue_id = issue_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public static ObservableList<Issue> getAllIssues() {
        ObservableList<Issue> issues = FXCollections.observableArrayList();

        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM issues");
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Issue issue = new Issue();
                issue.setIssueId(resultSet.getInt("issue_id"));
                issue.setTitle(resultSet.getString("title"));
                issue.setDescription(resultSet.getString("description"));
                issue.setStatus(resultSet.getString("status"));
                issue.setCreatedAt(resultSet.getTimestamp("created_at"));
                issue.setUserName(getUserNameByUserId(connection, resultSet.getInt("userId")));
                issue.setAssignee(getUserNameByUserId(connection, resultSet.getInt("assignee")));
                issue.setReportsTo(getUserNameByUserId(connection, resultSet.getInt("reports_to")));
                issues.add(issue);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return issues;
    }

    public static ObservableList<Issue> getIssuesForUser(String username) {
        ObservableList<Issue> issues = FXCollections.observableArrayList();

        String sql = "SELECT * FROM issues WHERE assignee = (SELECT userId FROM users WHERE username = ?)";

        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Issue issue = new Issue();
                    issue.setIssueId(resultSet.getInt("issue_id"));
                    issue.setTitle(resultSet.getString("title"));
                    issue.setDescription(resultSet.getString("description"));
                    issue.setStatus(resultSet.getString("status"));
                    issue.setCreatedAt(resultSet.getTimestamp("created_at"));
                    issue.setUserName(getUserNameByUserId(connection, resultSet.getInt("userId")));
                    issue.setAssignee(getUserNameByUserId(connection, resultSet.getInt("assignee")));
                    issues.add(issue);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return issues;
    }

    private static String getUserNameByUserId(Connection connection, int userId) throws SQLException {
        String sql = "SELECT username FROM users WHERE userId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("username");
                }
            }
        }

        return null; // Return null or handle it appropriately if the username is not found
    }

    public void addIssue() {
        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword())) {

            // Check if the specified username exists
            int userId = getUserIdByUserName(connection, getAssignee());

            if (userId != -1) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO issues (title, description, status, assignee) VALUES (?, ?, ?, ?)")) {

                    preparedStatement.setString(1, getTitle());
                    preparedStatement.setString(2, getDescription());
                    preparedStatement.setString(3, getStatus());
                    preparedStatement.setString(4, getAssignee());
                    preparedStatement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("User not found. Please specify a valid user.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getUserIdByUserName(Connection connection, String userName) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("SELECT userId FROM users WHERE username = ?")) {
            preparedStatement.setString(1, userName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("userId");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updateIssue() {
        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE issues SET title=?, description=?, status=?, assignee=? WHERE issueId=?")) {

            preparedStatement.setString(1, getTitle());
            preparedStatement.setString(2, getDescription());
            preparedStatement.setString(3, getStatus());
            preparedStatement.setInt(4, getUserId());
            preparedStatement.setInt(5, getIssueId());
            preparedStatement.setString(6, getAssignee());
            preparedStatement.setString(7, getReportsTo());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteIssue() {
        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "DELETE FROM issues WHERE issueId=?")) {

            preparedStatement.setInt(1, getIssueId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getReportsTo() {
        return reportsTo;
    }

    public void setReportsTo(String reportsTo) {
        this.reportsTo = reportsTo;
    }
}
