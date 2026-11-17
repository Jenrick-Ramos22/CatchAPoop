
package com.catchapoop.database;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() throws SQLException {

        // Read schema.sql from Maven resources
        try (InputStream input =
                DatabaseInitializer.class.getResourceAsStream(
                    "/schema.sql"
                )) {

            if (input == null) {
                throw new SQLException("schema.sql not found!");
            }

            String sql = new String(
                input.readAllBytes(),
                StandardCharsets.UTF_8
            );

            try (Connection conn =
                    DatabaseConnection.getConnection()) {

                conn.setAutoCommit(false);

                try (Statement stmt = conn.createStatement()) {

                    // Execute each SQL statement
                    for (String command : sql.split(";")) {

                        if (!command.isBlank()) {
                            stmt.execute(command);
                        }
                    }

                    conn.commit();

                    System.out.println(
                        "Database initialized successfully!"
                    );

                } catch (SQLException e) {

                    conn.rollback();
                    throw e;
                }
            }

        } catch (java.io.IOException e) {

            throw new SQLException(
                "Cannot read schema.sql", e
            );
        }
    }
}
