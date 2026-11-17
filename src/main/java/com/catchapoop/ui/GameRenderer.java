package com.catchapoop.ui;

import com.catchapoop.model.GameSession;
import java.awt.*;

/** Draws the HUD and overlays. Game rules stay in GameSession. */
public class GameRenderer {
    private static final int WIDTH = 450;
    private static final int HEIGHT = 700;
    private final GameAssets assets;

    public GameRenderer(GameAssets assets) {
        this.assets = assets;
    }

    // Draw hearts and text-only scores
    public void drawHUD(Graphics g, GameSession session, int highScore, Component observer) {

        // Hearts PNG
        assets.draw(g, "Official Lives.png", 10, 12, 105, 43, observer);

        // Red X marks for lost hearts
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(new Color(210, 35, 35));
        g2.setStroke(new BasicStroke(3));

        for (int i = session.getHearts(); i < 3; i++) {

            int x = 36 + i * 29;
            int y = 33;

            g2.drawLine(
                x - 7, y - 7,
                x + 7, y + 7
            );

            g2.drawLine(
                x + 7, y - 7,
                x - 7, y + 7
            );
        }

        g2.dispose();

        // Text-only Score and High Score
        Graphics2D textGraphics =
                (Graphics2D) g.create();

        textGraphics.setFont(
            new Font("Monospaced", Font.BOLD, 17)
        );

        textGraphics.setColor(
            new Color(45, 55, 65)
        );

        // Right-align both values to prevent overlap
        String scoreText =
                "SCORE: " + session.getScore();

        String highText =
                "HIGH: " + Math.max(
                    highScore,
                    session.getScore()
                );

        FontMetrics fm = textGraphics.getFontMetrics();

        textGraphics.drawString(
            scoreText,
            WIDTH - 12 - fm.stringWidth(scoreText),
            30
        );

        textGraphics.drawString(
            highText,
            WIDTH - 12 - fm.stringWidth(highText),
            53
        );

        textGraphics.dispose();

        // Rainbow progress
        g.setFont(
            new Font("Monospaced", Font.BOLD, 15)
        );

        g.setColor(
            new Color(190, 40, 160)
        );

        g.drawString(
            "RAINBOW " + session.getRainbowCount() + "/5",
            18, 82
        );

        // Rainbow boost timer
        if (session.isBoostActive()) {

            g.setColor(Color.YELLOW);

            g.drawString(
                "10X BOOST: "
                + session.getBoostSecondsRemaining()
                + "s",
                18, 104
            );
        }
    }

    // Pause screen
    public void drawPauseScreen(Graphics g, Component observer) {

        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        assets.draw(g, "Paused Panel.png", 75, 210, 300, 250, observer);

        g.setColor(Color.WHITE);

        g.setFont(
            new Font("Monospaced", Font.BOLD, 22)
        );

        g.drawString("PAUSED", 175, 320);
    }

    // Game Over screen
    public void drawGameOver(Graphics g, GameSession session, String saveStatus, Component observer) {

        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // PNG already contains GAME OVER title
        assets.draw(g, "Game Over Panel.png", 45, 175, 360, 325, observer);

        // Score number only
        g.setFont(
            new Font("Monospaced", Font.BOLD, 25)
        );

        g.setColor(new Color(73, 43, 92));

        drawCentered(
            g,
            String.valueOf(session.getScore()),
            345
        );

        // Database status
        g.setFont(
            new Font("Monospaced", Font.BOLD, 12)
        );

        g.setColor(new Color(45, 110, 60));

        drawCentered(g, saveStatus, 395);
    }

    // Center text horizontally
    private void drawCentered(
            Graphics g, String text, int y) {

        int x = (WIDTH - g.getFontMetrics()
                .stringWidth(text)) / 2;

        g.drawString(text, x, y);
    }
}
