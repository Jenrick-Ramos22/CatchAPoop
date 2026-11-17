
package com.catchapoop.database;

import com.catchapoop.dao.ScoreDAO;
import java.sql.SQLException;

public class ScoreDatabaseTest {

    public static void main(String[] args) {

        try {
            // Initialize database
            DatabaseInitializer.initialize();

            ScoreDAO scoreDAO = new ScoreDAO();

            // Existing player and character
            int playerId = 1;
            int characterId = 3;

            // Test score
            int testScore = 250;

            // Save score
            scoreDAO.saveScore(
                playerId,
                characterId,
                testScore
            );

            System.out.println("Score saved successfully!");
            System.out.println("Player ID: " + playerId);
            System.out.println("Character ID: " + characterId);
            System.out.println("Saved Score: " + testScore);

            // Retrieve highest score
            int highScore =
                    scoreDAO.getHighestScore(playerId);

            System.out.println("Highest Score: " + highScore);

            if (highScore >= testScore) {
                System.out.println("SCORE TEST PASSED!");
            } else {
                System.out.println("SCORE TEST FAILED!");
            }

        } catch (SQLException e) {
            System.out.println(
                "Database error: " + e.getMessage()
            );
        }
    }
}
