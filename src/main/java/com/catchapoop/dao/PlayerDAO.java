
package com.catchapoop.dao;

import com.catchapoop.database.DatabaseConnection;
import com.catchapoop.model.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerDAO {

    // CREATE
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
                    "SELECT id, name FROM players WHERE id = last_insert_rowid()");
                 ResultSet rs = query.executeQuery()) {

                if (rs.next()) {
                    return new Player(
                        rs.getInt("id"),
                        rs.getString("name")
                    );
                }
            }

            throw new SQLException("Could not retrieve the new player.");
        }
    }

    // READ
    public Player getPlayerById(int id) throws SQLException {

        String sql = "SELECT id, name FROM players WHERE id = ?";

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

    // UPDATE
    public boolean updatePlayer(int id, String newName)
            throws SQLException {

        String sql = "UPDATE players SET name = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newName);
            pstmt.setInt(2, id);

            int rows = pstmt.executeUpdate();

            return rows == 1;
        }
    }

    // DELETE
    public boolean deletePlayer(int id) throws SQLException {

        String sql = "DELETE FROM players WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            return rows == 1;
        }
    }
}
