
package com.catchapoop.service;

import com.catchapoop.dao.ScoreDAO;

import java.sql.SQLException;
import java.util.List;

public class ScoreService {

    private final ScoreDAO scoreDAO = new ScoreDAO();

    // CREATE - Save a completed game score
    public void saveScore(int playerId, int characterId, int score)
            throws SQLException {

        if (playerId <= 0) {
            throw new IllegalArgumentException(
                "Invalid player ID!"
            );
        }

        if (characterId <= 0) {
            throw new IllegalArgumentException(
                "Invalid character ID!"
            );
        }

        if (score < 0) {
            throw new IllegalArgumentException(
                "Score cannot be negative!"
            );
        }

        scoreDAO.saveScore(playerId, characterId, score);
    }

    // READ - Get a player's highest score
    public int getHighestScore(int playerId)
            throws SQLException {

        if (playerId <= 0) {
            throw new IllegalArgumentException(
                "Invalid player ID!"
            );
        }

        return scoreDAO.getHighestScore(playerId);
    }

    // READ - Get the leaderboard
    public List<String> getLeaderboard()
            throws SQLException {

        return scoreDAO.getLeaderboard();
    }
}
