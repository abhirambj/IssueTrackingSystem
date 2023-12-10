package com.itmd510.issuetrackingapplication.models;

import com.itmd510.issuetrackingapplication.DB.DBConnector;
import com.itmd510.issuetrackingapplication.config.ConfigLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Project {

    private static final List<Project> allProjects = new ArrayList<>();

    private int projectId;
    private String projectName;
    private String clientName;
    private String projectLead;

    public Project() {
        // Default constructor
    }

    public Project(int projectId, String projectName, String clientName, String projectLead) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.clientName = clientName;
        this.projectLead = projectLead;
    }

    // Getter and Setter methods

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getProjectLead() {
        return projectLead;
    }

    public void setProjectLead(String projectLead) {
        this.projectLead = projectLead;
    }


    public void addProject() {
        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword())) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO its_projects (projectName, clientName, projectLead) VALUES (?, ?, ?)")) {

                preparedStatement.setString(1, getProjectName());
                preparedStatement.setString(2, getClientName());
                preparedStatement.setString(3, getProjectLead());
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static List<Project> getAllProjects() {
        List<Project> allProjects = new ArrayList<>();

        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword())) {

            String query = "SELECT * FROM its_projects";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int projectId = resultSet.getInt("projectId");
                    String projectName = resultSet.getString("projectName");
                    String clientName = resultSet.getString("clientName");
                    String projectLead = resultSet.getString("projectLead");

                    Project project = new Project(projectId, projectName, clientName, projectLead);
                    allProjects.add(project);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately, log messages, or show alerts as needed
        }

        return allProjects;
    }

    // Override toString method for better debugging or display
    @Override
    public String toString() {
        return "Project{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", clientName='" + clientName + '\'' +
                ", projectLead='" + projectLead + '\'' +
                '}';
    }

    public void updateProject() {
        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword())) {

            // Update project details in the database
            String updateQuery = "UPDATE its_projects SET projectName=?, clientName=?, projectLead=? WHERE projectId=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, this.projectName);
                preparedStatement.setString(2, this.clientName);
                preparedStatement.setString(3, this.projectLead);
                preparedStatement.setInt(4, this.projectId);

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Project updated successfully in the database.");
                } else {
                    System.out.println("Failed to update project in the database.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately, log messages, or show alerts as needed
        }
    }

    public void deleteProject() {
        String query = "DELETE FROM its_projects WHERE projectId = ?";
        try (Connection connection = DBConnector.getConnection(ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(), ConfigLoader.getDatabasePassword());
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, this.projectId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately, log messages, or show alerts as needed
        }
    }

}
