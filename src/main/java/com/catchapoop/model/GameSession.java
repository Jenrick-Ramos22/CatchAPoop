
package com.catchapoop.model;

public class GameSession {

    private int score = 0;
    private int hearts = 3;
    private int rainbowCount = 0;

    // Gameplay time measured in milliseconds
    private long gameTimeMillis = 0;

    // Rainbow boost deadline in gameplay time
    private long boostEndTime = 0;

    private int nextBombRushScore = 50;
    private int pendingBombRushes = 0;

    private boolean paused = false;

    // Advance time only during active gameplay
    public void updateTime(long elapsedMillis) {

        if (!paused && !isGameOver() && elapsedMillis > 0) {
            gameTimeMillis += elapsedMillis;
        }
    }

    // Add points for brown or gold poop
    public void addScore(int basePoints) {

        if (hearts <= 0 || paused || basePoints <= 0) {
            return;
        }

        int multiplier = isBoostActive() ? 10 : 1;

        score += basePoints * multiplier;

        // Bomb Rush every 50 points
        while (score >= nextBombRushScore) {
            pendingBombRushes++;
            nextBombRushScore += 50;
        }
    }

    // Catch tissue: minus 2 points
    public void catchTissue() {

        if (hearts <= 0 || paused) {
            return;
        }

        score = Math.max(0, score - 2);
    }

    // Collect rainbow poop
    public void catchRainbow() {

        if (hearts <= 0 || paused || isBoostActive()) {
            return;
        }

        rainbowCount++;

        if (rainbowCount >= 5) {
            rainbowCount = 0;

            // Boost lasts 30 seconds of gameplay time
            boostEndTime = gameTimeMillis + 30000;
        }
    }

    // Catch bomb
    public void catchBomb() {

        if (hearts > 0 && !paused) {
            hearts--;
        }
    }

    // Pause game
    public void pauseGame() {
        if (!isGameOver()) {
            paused = true;
        }
    }

    // Resume game
    public void resumeGame() {
        paused = false;
    }

    // Check rainbow boost
    public boolean isBoostActive() {
        return gameTimeMillis < boostEndTime;
    }

    // Check Bomb Rush
    public boolean consumeBombRush() {

        if (pendingBombRushes > 0) {
            pendingBombRushes--;
            return true;
        }

        return false;
    }

    public boolean isGameOver() {
        return hearts <= 0;
    }

    public int getScore() {
        return score;
    }

    public int getHearts() {
        return hearts;
    }

    public int getRainbowCount() {
        return rainbowCount;
    }

    // Get remaining boost seconds
    public long getBoostSecondsRemaining() {

        long remaining = boostEndTime - gameTimeMillis;

        return Math.max(0, (remaining + 999) / 1000);
    }
}
