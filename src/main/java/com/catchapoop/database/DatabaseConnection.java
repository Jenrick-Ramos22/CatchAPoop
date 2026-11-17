
package com.catchapoop.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DatabaseConnection {

    public static Connection getConnection()
            throws SQLException {

        try {
            Path folder = Path.of(
                System.getProperty("user.home"),
                ".catchapoop"
            );

            Files.createDirectories(folder);

            Path database = folder.resolve("catchapoop.db");

            Connection conn = DriverManager.getConnection(
                "jdbc:sqlite:" + database.toAbsolutePath()
            );

            try (var statement = conn.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
            }

            return conn;

        } catch (java.io.IOException e) {
            throw new SQLException(
                "Could not create database folder.", e
            );
        }
    }
}
