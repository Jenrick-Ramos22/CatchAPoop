
package com.catchapoop.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CharacterDatabaseTest {

    public static void main(String[] args) {

        try {
            DatabaseInitializer.initialize();

            String sql =
                "SELECT id, name, image_path "
                + "FROM characters ORDER BY id";

            try (Connection conn =
                    DatabaseConnection.getConnection();
                 PreparedStatement pstmt =
                    conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                System.out.println("SAVED CHARACTERS:");

                int count = 0;

                while (rs.next()) {

                    System.out.println(
                        rs.getInt("id") + " - "
                        + rs.getString("name") + " - "
                        + rs.getString("image_path")
                    );

                    count++;
                }

                System.out.println(
                    "Total characters: " + count
                );
            }

        } catch (SQLException e) {
            System.out.println(
                "Database error: " + e.getMessage()
            );
        }
    }
}
