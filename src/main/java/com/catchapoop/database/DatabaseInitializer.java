
package com.catchapoop.database;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void initialize() throws SQLException {

        Path schemaPath = Path.of("database", "schema.sql");

        try {
            String sql = Files.readString(schemaPath);

            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement()) {

                for (String command : sql.split(";")) {
                    if (!command.isBlank()) {
                        stmt.execute(command);
                    }
                }

                System.out.println("Database tables created!");
            }

        } catch (IOException e) {
            throw new SQLException(
                "Cannot read schema.sql: " + e.getMessage(), e
            );
        }
    }
}
