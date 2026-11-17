
package com.catchapoop.model;

public class GameSession {

    private int score = 0;
    private int hearts = 3;
    private int rainbowCount = 0;
    private long boostEndTime = 0;
    private int nextBombRushScore = 50;
    private int pendingBombRushes = 0;

    // Add points for catching brown or gold poop
    public void addScore(int basePoints) {

        if (hearts <= 0 || basePoints <= 0) {
            return;
        }

        int multiplier = isBoostActive() ? 10 : 1;
        score += basePoints * multiplier;

        // Detect every crossed 50-point milestone
        while (score >= nextBombRushScore) {
            pendingBombRushes++;
            nextBombRushScore += 50;
        }
    }

    // Collect rainbow poop
    public void catchRainbow() {

        if (hearts <= 0 || isBoostActive()) {
            return;
        }

        rainbowCount++;

        if (rainbowCount >= 5) {
            rainbowCount = 0;
            boostEndTime = System.currentTimeMillis() + 30000;
        }
    }

    // Catch a bomb
    public void catchBomb() {

        if (hearts > 0) {
            hearts--;
        }
    }

    // Check whether the 10x multiplier is active
    public boolean isBoostActive() {
        return System.currentTimeMillis() < boostEndTime;
    }

    // Check whether a Bomb Rush needs to spawn
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

    public long getBoostSecondsRemaining() {
        long remaining = boostEndTime - System.currentTimeMillis();
        return Math.max(0, (remaining + 999) / 1000);
    }
}
