package com.itmd510.issuetrackingapplication.models;

import com.itmd510.issuetrackingapplication.DB.DBConnector;
import com.itmd510.issuetrackingapplication.config.ConfigLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

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
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM its_issues");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Issue issue = new Issue();
                issue.setIssueId(resultSet.getInt("issueId"));
                issue.setTitle(resultSet.getString("title"));
                issue.setDescription(resultSet.getString("description"));
                issue.setStatus(resultSet.getString("status"));
                issue.setCreatedAt(resultSet.getTimestamp("created_at"));
                issue.setAssignee(resultSet.getString("assignee"));
                issue.setReportsTo("reportsTo");
                issues.add(issue);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return issues;
    }

    public static ObservableList<Issue> getIssuesForUser(String username) {
        ObservableList<Issue> issues = FXCollections.observableArrayList();

        String sql = "SELECT * FROM its_issues WHERE assignee = (SELECT username FROM its_users WHERE username = ?)";

        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Issue issue = new Issue();
                    issue.setIssueId(resultSet.getInt("issueId"));
                    issue.setTitle(resultSet.getString("title"));
                    issue.setDescription(resultSet.getString("description"));
                    issue.setStatus(resultSet.getString("status"));
                    issue.setCreatedAt(resultSet.getTimestamp("created_at"));
                    issue.setAssignee(resultSet.getString("assignee"));
                    issue.setReportsTo(resultSet.getString("reportsTo"));
                    issues.add(issue);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return issues;
    }

    public static ObservableList<Issue> getIssuesForManager(String username) {
        ObservableList<Issue> issues = FXCollections.observableArrayList();

        String sql = "SELECT * FROM its_issues WHERE assignee = ? OR reportsTo = ?";

        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Issue issue = new Issue();
                    issue.setIssueId(resultSet.getInt("issueId"));
                    issue.setTitle(resultSet.getString("title"));
                    issue.setDescription(resultSet.getString("description"));
                    issue.setStatus(resultSet.getString("status"));
                    issue.setCreatedAt(resultSet.getTimestamp("created_at"));
                    issue.setAssignee(resultSet.getString("assignee"));
                    issue.setReportsTo(resultSet.getString("reportsTo"));
                    System.out.println(issue);
                    issues.add(issue);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return issues;
    }


    private static String getUserNameByUserId(Connection connection, int userId) throws SQLException {
        String sql = "SELECT username FROM its_users WHERE userId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("username");
                }
            }
        }

        return null;
    }

    public void addIssue() {
        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword())) {

            int userId = getUserIdByUserName(connection, getAssignee());

            if (userId != -1) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO its_issues (title, description, status, assignee, reportsTo, created_at) VALUES (?, ?, ?, ?, ?, ?)")) {

                    preparedStatement.setString(1, getTitle());
                    preparedStatement.setString(2, getDescription());
                    preparedStatement.setString(3, getStatus());
                    preparedStatement.setString(4, getAssignee());
                    preparedStatement.setString(5, getReportsTo());
                    preparedStatement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
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
                .prepareStatement("SELECT userId FROM its_users WHERE username = ?")) {
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
                     "UPDATE its_issues SET title=?, description=?, status=?, assignee=?, reportsTo=? WHERE issueId=?")) {

            preparedStatement.setString(1, getTitle());
            preparedStatement.setString(2, getDescription());
            preparedStatement.setString(3, getStatus());
            preparedStatement.setString(4, getAssignee());
            preparedStatement.setString(5, getReportsTo());
            preparedStatement.setInt(6, getIssueId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteIssue() {
        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM its_issues WHERE issueId=?")) {

            preparedStatement.setInt(1, getIssueId());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Issue with ID " + getIssueId() + " deleted successfully.");
            } else {
                System.out.println("Issue with ID " + getIssueId() + " not found or couldn't be deleted.");
            }

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

    @Override
    public String toString() {
        return "Title: " + getTitle() + "\nDescription: " + getDescription() + "\nStatus: " + getStatus();
    }

}
