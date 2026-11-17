
package com.catchapoop;

import com.catchapoop.model.GameSession;

public class CatchAPoop {

    public static void main(String[] args) {

        GameSession game = new GameSession();

        // TEST 1: Normal scoring
        game.addScore(1);
        game.addScore(5);

        System.out.println("Score: " + game.getScore());
        System.out.println("Expected: 6");

        // TEST 2: Rainbow power-up
        for (int i = 0; i < 5; i++) {
            game.catchRainbow();
        }

        System.out.println("Boost active: " + game.isBoostActive());

        // TEST 3: 10x multiplier
        game.addScore(5);

        System.out.println("Score after boost: " + game.getScore());
        System.out.println("Expected: 56");

        // TEST 4: Bomb Rush
        System.out.println(
            "Bomb Rush triggered: " + game.consumeBombRush()
        );

        // TEST 5: Hearts and Game Over
        game.catchBomb();
        game.catchBomb();
        game.catchBomb();

        System.out.println("Hearts: " + game.getHearts());
        System.out.println("Game Over: " + game.isGameOver());
    }
}
