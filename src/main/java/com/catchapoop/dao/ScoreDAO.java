
package com.catchapoop.dao;

import com.catchapoop.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScoreDAO {

    // CREATE - Save a completed game score
    public void saveScore(int playerId, int characterId, int score)
            throws SQLException {

        String sql = "INSERT INTO scores "
                + "(player_id, character_id, score) "
                + "VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playerId);
            pstmt.setInt(2, characterId);
            pstmt.setInt(3, score);

            int rows = pstmt.executeUpdate();

            if (rows != 1) {
                throw new SQLException("Score was not saved.");
            }
        }
    }

    // READ - Highest score for one player
    public int getHighestScore(int playerId)
            throws SQLException {

        String sql = "SELECT MAX(score) AS high_score "
                + "FROM scores WHERE player_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playerId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("high_score");
                }
            }
        }

        return 0;
    }

    // READ - Leaderboard of players and their best scores
    public List<String> getLeaderboard() throws SQLException {

        List<String> leaderboard = new ArrayList<>();

        String sql =
                "SELECT p.name, MAX(s.score) AS high_score "
                + "FROM players p "
                + "JOIN scores s ON p.id = s.player_id "
                + "GROUP BY p.id, p.name "
                + "ORDER BY high_score DESC, p.name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            int rank = 1;

            while (rs.next()) {

                String name = rs.getString("name");
                int highScore = rs.getInt("high_score");

                leaderboard.add(
                    rank + ". " + name + " - " + highScore
                );

                rank++;
            }
        }

        return leaderboard;
    }
}
