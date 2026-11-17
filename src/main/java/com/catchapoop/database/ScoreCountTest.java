
package com.catchapoop.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ScoreCountTest {

    public static void main(String[] args) {

        String sql =
            "SELECT p.id, p.name, COUNT(s.id) AS total_games "
            + "FROM players p "
            + "LEFT JOIN scores s ON p.id = s.player_id "
            + "WHERE p.name = ? "
            + "GROUP BY p.id, p.name";

        try (Connection conn =
                DatabaseConnection.getConnection();
             PreparedStatement pstmt =
                conn.prepareStatement(sql)) {

            pstmt.setString(1, "RetryTest");

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    int count = rs.getInt("total_games");

                    System.out.println(
                        "Player: " + rs.getString("name")
                    );

                    System.out.println(
                        "Completed games saved: " + count
                    );

                    if (count == 2) {
                        System.out.println(
                            "RETRY SCORE TEST PASSED!"
                        );
                    } else {
                        System.out.println(
                            "RETRY SCORE TEST FAILED!"
                        );
                    }

                } else {
                    System.out.println(
                        "RetryTest player not found."
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println(
                "Database error: " + e.getMessage()
            );
        }
    }
}
