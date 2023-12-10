package com.itmd510.issuetrackingapplication.models;

import com.itmd510.issuetrackingapplication.DB.DBConnector;
import com.itmd510.issuetrackingapplication.config.ConfigLoader;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Loader {
    public void createTables() {
        String[] createTableQueries = {
                "CREATE TABLE IF NOT EXISTS its_roles (role_id INT AUTO_INCREMENT PRIMARY KEY, role_name VARCHAR(255) NOT NULL)",
                "CREATE TABLE IF NOT EXISTS its_users (userId INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, email VARCHAR(255) NULL, roleId INT NULL, managerId INT NULL, CONSTRAINT username UNIQUE (username), CONSTRAINT users_ibfk_1 FOREIGN KEY (roleId) REFERENCES its_roles (role_id), CONSTRAINT users_ibfk_2 FOREIGN KEY (managerId) REFERENCES its_users (userId))",
                "CREATE INDEX roleId ON its_users (roleId)",
                "CREATE INDEX managerId ON its_users (managerId)",
                "CREATE TABLE IF NOT EXISTS its_projects (projectId INT AUTO_INCREMENT PRIMARY KEY, projectName VARCHAR(255) NOT NULL, clientName VARCHAR(255) NOT NULL, projectLead INT NULL, CONSTRAINT projects_ibfk_1 FOREIGN KEY (projectLead) REFERENCES its_users (userId))",
                "CREATE INDEX projectLead ON its_projects (projectLead)",
                "CREATE TABLE IF NOT EXISTS its_issues (issueId INT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(255) NOT NULL, description TEXT NULL, assignee VARCHAR(255) NULL, reportsTo VARCHAR(255) NULL, status VARCHAR(255) DEFAULT 'To Do' NULL, created_at TIMESTAMP NULL, CONSTRAINT issues_ibfk_1 FOREIGN KEY (assignee) REFERENCES its_users (username), CONSTRAINT issues_ibfk_2 FOREIGN KEY (reportsTo) REFERENCES its_users (username))",
                "CREATE INDEX assignee ON its_issues (assignee)",
                "CREATE INDEX reportsTo ON its_issues (reportsTo)"
        };

        try (Connection connection = DBConnector.getConnection(
                ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(),
                ConfigLoader.getDatabasePassword())) {

            if (connection != null) {
                Statement statement = connection.createStatement();

                for (String query : createTableQueries) {
                    statement.executeUpdate(query);
                }
                System.out.println("Tables created successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging the error message or handling it more gracefully
        }
    }

    public void dropTables() {
        String[] dropTableQueries = {
                "DROP TABLE IF EXISTS its_issues",
                "DROP TABLE IF EXISTS its_projects",
                "DROP TABLE IF EXISTS its_users",
                "DROP TABLE IF EXISTS its_roles"
        };

        try (Connection connection = DBConnector.getConnection(
                ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(),
                ConfigLoader.getDatabasePassword())) {

            if (connection != null) {
                Statement statement = connection.createStatement();

                for (String query : dropTableQueries) {
                    statement.executeUpdate(query);
                }
                System.out.println("Tables dropped successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging the error message or handling it more gracefully
        }
    }

    public  void updateTables() {
        String[] updateTableQueries = {
                "ALTER TABLE its_issues MODIFY COLUMN assignee VARCHAR(255)",
                "ALTER TABLE its_issues MODIFY COLUMN reportsTo VARCHAR(255)",
                "ALTER TABLE its_issues ADD CONSTRAINT issues_ibfk_1 FOREIGN KEY (assignee) REFERENCES its_users (username)",
                "ALTER TABLE its_issues ADD CONSTRAINT issues_ibfk_2 FOREIGN KEY (reportsTo) REFERENCES its_users (username)",
                "ALTER TABLE its_issues MODIFY COLUMN status VARCHAR(255) DEFAULT 'To Do'",
        };

        try (Connection connection = DBConnector.getConnection(
                ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(),
                ConfigLoader.getDatabasePassword())) {

            if (connection != null) {
                Statement statement = connection.createStatement();

                for (String query : updateTableQueries) {
                    statement.executeUpdate(query);
                }
                System.out.println("Tables updated successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging the error message or handling it more gracefully
        }
    }
    public void retrieveTableNames() {
        try (Connection connection = DBConnector.getConnection(
                ConfigLoader.getDatabaseUrl(),
                ConfigLoader.getDatabaseUser(),
                ConfigLoader.getDatabasePassword())) {

            if (connection != null) {
                DatabaseMetaData metaData = connection.getMetaData();

                // Retrieving specific tables by name
                String[] tableNames = {"its_roles", "its_users", "its_projects", "its_issues"};
                System.out.println("Tables:");
                for (String tableName : tableNames) {
                    ResultSet resultSet = metaData.getTables(null, null, tableName, null);
                    while (resultSet.next()) {
                        System.out.println(resultSet.getString("TABLE_NAME"));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging the error message or handling it more gracefully
        }
    }

    public static void main(String[] args) {
        Loader loader = new Loader();
        loader.updateTables();
//        loader.createTables();
        loader.retrieveTableNames();
    }
}
