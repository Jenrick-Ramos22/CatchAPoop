
package com.catchapoop.database;

import com.catchapoop.dao.ScoreDAO;
import java.sql.SQLException;

public class ScorePersistenceTest {

    public static void main(String[] args) {

        try {
            ScoreDAO scoreDAO = new ScoreDAO();

            int playerId = 1;

            // READ ONLY - Does not insert a new score
            int highScore = scoreDAO.getHighestScore(playerId);

            System.out.println("SCORE PERSISTENCE TEST");
            System.out.println("Player ID: " + playerId);
            System.out.println("Saved High Score: " + highScore);

            if (highScore >= 250) {
                System.out.println("PERSISTENCE TEST PASSED!");
            } else {
                System.out.println("PERSISTENCE TEST FAILED!");
            }

        } catch (SQLException e) {
            System.out.println(
                "Database error: " + e.getMessage()
            );
        }
    }
}
