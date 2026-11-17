
package com.catchapoop.dao;

import com.catchapoop.database.DatabaseConnection;
import com.catchapoop.model.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO {

    // CREATE - Add a new player
    public Player addPlayer(String name) throws SQLException {

        String sql = "INSERT INTO players (name) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            int rows = pstmt.executeUpdate();

            if (rows != 1) {
                throw new SQLException("Player was not saved.");
            }

            try (PreparedStatement query = conn.prepareStatement(
                    "SELECT id, name FROM players "
                    + "WHERE id = last_insert_rowid()");
                 ResultSet rs = query.executeQuery()) {

                if (rs.next()) {
                    return new Player(
                        rs.getInt("id"),
                        rs.getString("name")
                    );
                }
            }

            throw new SQLException(
                "Could not retrieve the new player."
            );
        }
    }

    // READ - Find a player by ID
    public Player getPlayerById(int id) throws SQLException {

        String sql =
                "SELECT id, name FROM players WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return new Player(
                        rs.getInt("id"),
                        rs.getString("name")
                    );
                }
            }
        }

        return null;
    }

    // READ - Retrieve all saved players
    public List<Player> getAllPlayers() throws SQLException {

        List<Player> players = new ArrayList<>();

        String sql =
                "SELECT id, name FROM players ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                Player player = new Player(
                    rs.getInt("id"),
                    rs.getString("name")
                );

                players.add(player);
            }
        }

        return players;
    }

    // UPDATE - Change a player's name
    public boolean updatePlayer(int id, String newName)
            throws SQLException {

        String sql =
                "UPDATE players SET name = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newName);
            pstmt.setInt(2, id);

            return pstmt.executeUpdate() == 1;
        }
    }

    // DELETE - Remove player and their saved scores
    public boolean deletePlayer(int id) throws SQLException {

        String deleteScores =
                "DELETE FROM scores WHERE player_id = ?";

        String deletePlayer =
                "DELETE FROM players WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Start a database transaction
            conn.setAutoCommit(false);

            try {

                // Step 1: Delete the player's scores
                try (PreparedStatement pstmt =
                        conn.prepareStatement(deleteScores)) {

                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                }

                // Step 2: Delete the player
                int rowsDeleted;

                try (PreparedStatement pstmt =
                        conn.prepareStatement(deletePlayer)) {

                    pstmt.setInt(1, id);
                    rowsDeleted = pstmt.executeUpdate();
                }

                // Step 3: Save changes together
                conn.commit();

                return rowsDeleted == 1;

            } catch (SQLException e) {

                // Undo changes if an error occurs
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }
        }
    }
}
