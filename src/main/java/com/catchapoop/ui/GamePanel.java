
package com.catchapoop.ui;

import com.catchapoop.model.FallingObject;
import com.catchapoop.model.FallingObjectType;
import com.catchapoop.model.GameSession;
import com.catchapoop.model.CharacterType;
import com.catchapoop.model.Player;
import com.catchapoop.service.ScoreService;
import com.catchapoop.database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class GamePanel extends JPanel {

    // Game screen
    private static final int GAME_WIDTH = 600;
    private static final int GAME_HEIGHT = 450;

    // Player settings
    private static final int PLAYER_WIDTH = 80;
    private static final int PLAYER_HEIGHT = 80;
    private static final int MOVE_SPEED = 15;

    // Falling object settings
    private static final int POOP_SIZE = 45;
    private static final int POOP_SPEED = 4;

    // Moving butt settings
    private static final int BUTT_WIDTH = 90;
    private static final int BUTT_HEIGHT = 70;
    private static final int BUTT_SPEED = 7;
    private static final int BUTT_Y = 5;

    // Spawn settings
    private static final int SPAWN_INTERVAL = 50;
    private static final int BOMB_RUSH_COUNT = 6;

    // Game images
    private final Image background;
    private final Image character;
    private final Image brownPoop;
    private final Image goldPoop;
    private final Image rainbowPoop;
    private final Image bombImage;
    private final Image tissueImage;
    private final Image buttImage;

    // Game logic
    private final GameSession session = new GameSession();
    private final Random random = new Random();
    private final ScoreService scoreService = new ScoreService();

    // Selected player and character
    private final Player selectedPlayer;
    private final CharacterType selectedCharacter;

    // Falling objects
    private final ArrayList<FallingObject> fallingObjects =
            new ArrayList<>();

    // Positions
    private int playerX = 250;
    private final int playerY = 350;

    private int buttX = 250;
    private int buttDirection = 1;
    private int spawnCounter = 0;

    // Pause and gameplay clock
    private boolean paused = false;
    private long lastUpdateNanos = System.nanoTime();

    // Score saving
    private boolean scoreSaveStarted = false;
    private boolean scoreSaved = false;
    private String saveStatus = "";

    // Navigation
    private Runnable onRetry;
    private Runnable onHome;

    // Buttons
    private final JButton pauseButton = new JButton("PAUSE");
    private final JButton retryButton = new JButton("RETRY");
    private final JButton homeButton = new JButton("HOME");

    private final Timer gameTimer;

    public GamePanel(Player selectedPlayer,
                     CharacterType selectedCharacter) {

        if (selectedPlayer == null || selectedCharacter == null) {
            throw new IllegalArgumentException(
                "Player and character are required."
            );
        }

        this.selectedPlayer = selectedPlayer;
        this.selectedCharacter = selectedCharacter;

        setPreferredSize(
            new Dimension(GAME_WIDTH, GAME_HEIGHT)
        );

        setFocusable(true);
        setLayout(null);

        // Load sprites
        background = loadImage("Catch a Poop Background.png");
        character = loadImage(selectedCharacter.getImageFile());
        brownPoop = loadImage("Official Normal Poop.gif");
        goldPoop = loadImage("Official Gold Poop.gif");
        rainbowPoop = loadImage("Official Rainbow Poop.gif");
        bombImage = loadImage("Official Bomb.png");
        tissueImage = loadImage("Official Tissue Roll.png");
        buttImage = loadImage("Official Butt.png");

        setupControls();
        setupButtons();

        gameTimer = new Timer(16, e -> updateGame());
        gameTimer.start();
    }

    // Load images
    private Image loadImage(String filename) {

        URL url = getClass().getResource(
            "/images/" + filename
        );

        if (url == null) {
            System.out.println("Image not found: " + filename);
            return null;
        }

        return new ImageIcon(url).getImage();
    }

    // Keyboard movement
    private void setupControls() {

        InputMap inputMap = getInputMap(
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        ActionMap actionMap = getActionMap();

        inputMap.put(
            KeyStroke.getKeyStroke("LEFT"), "moveLeft"
        );

        inputMap.put(
            KeyStroke.getKeyStroke("RIGHT"), "moveRight"
        );

        actionMap.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (paused || session.isGameOver()) {
                    return;
                }

                playerX = Math.max(
                    0, playerX - MOVE_SPEED
                );

                repaint();
            }
        });

        actionMap.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (paused || session.isGameOver()) {
                    return;
                }

                playerX = Math.min(
                    GAME_WIDTH - PLAYER_WIDTH,
                    playerX + MOVE_SPEED
                );

                repaint();
            }
        });
    }

    // Setup Pause, Retry, and Home
    private void setupButtons() {

        pauseButton.setBounds(470, 10, 110, 35);
        retryButton.setBounds(150, 320, 140, 40);
        homeButton.setBounds(310, 320, 140, 40);

        retryButton.setVisible(false);
        homeButton.setVisible(false);
        retryButton.setEnabled(false);
        homeButton.setEnabled(false);

        pauseButton.addActionListener(e -> togglePause());

        retryButton.addActionListener(e -> {
            if (session.isGameOver()
                    && scoreSaved
                    && onRetry != null) {
                stopGame();
                onRetry.run();
            }
        });

        homeButton.addActionListener(e -> {
            if (session.isGameOver()
                    && scoreSaved
                    && onHome != null) {
                stopGame();
                onHome.run();
            }
        });

        add(pauseButton);
        add(retryButton);
        add(homeButton);
    }

    // Pause or resume
    private void togglePause() {

        if (session.isGameOver()) {
            return;
        }

        paused = !paused;

        if (paused) {
            session.pauseGame();
            pauseButton.setText("RESUME");
        } else {
            session.resumeGame();
            pauseButton.setText("PAUSE");
        }

        // Reset clock so paused time is never counted
        lastUpdateNanos = System.nanoTime();

        repaint();
    }

    public void setOnRetry(Runnable onRetry) {
        this.onRetry = onRetry;
    }

    public void setOnHome(Runnable onHome) {
        this.onHome = onHome;
    }

    public void stopGame() {
        gameTimer.stop();
    }

    // Move butt
    private void moveButt() {

        buttX += BUTT_SPEED * buttDirection;

        if (buttX <= 0) {
            buttX = 0;
            buttDirection = 1;
        }

        if (buttX >= GAME_WIDTH - BUTT_WIDTH) {
            buttX = GAME_WIDTH - BUTT_WIDTH;
            buttDirection = -1;
        }
    }

    // Spawn objects
    private void spawnObject() {

        int objectX = buttX
                + (BUTT_WIDTH - POOP_SIZE) / 2;

        int chance = random.nextInt(100);

        FallingObjectType type;

        // Bomb 25%, Rainbow 10%, Gold 20%
        // Tissue 15%, Brown 30%
        if (chance < 25) {
            type = FallingObjectType.BOMB;
        } else if (chance < 35) {
            type = FallingObjectType.RAINBOW;
        } else if (chance < 55) {
            type = FallingObjectType.GOLD;
        } else if (chance < 70) {
            type = FallingObjectType.TISSUE;
        } else {
            type = FallingObjectType.BROWN;
        }

        fallingObjects.add(new FallingObject(
            objectX,
            BUTT_Y + BUTT_HEIGHT,
            POOP_SIZE,
            POOP_SIZE,
            POOP_SPEED,
            type
        ));
    }

    // Bomb Rush
    private void spawnBombRush() {

        for (int i = 0; i < BOMB_RUSH_COUNT; i++) {

            int bombX = random.nextInt(
                GAME_WIDTH - POOP_SIZE + 1
            );

            fallingObjects.add(new FallingObject(
                bombX,
                -POOP_SIZE - (i * 35),
                POOP_SIZE,
                POOP_SIZE,
                POOP_SPEED,
                FallingObjectType.BOMB
            ));
        }

        System.out.println("BOMB RUSH ACTIVATED!");
    }

    // Find character ID
    private int findCharacterId(String name)
            throws SQLException {

        String sql =
            "SELECT id FROM characters WHERE name = ?";

        try (Connection conn =
                DatabaseConnection.getConnection();
             PreparedStatement pstmt =
                conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        throw new SQLException(
            "Character not found: " + name
        );
    }

    // Save score once
    private void saveFinalScore() {

        if (scoreSaveStarted) {
            return;
        }

        scoreSaveStarted = true;
        saveStatus = "Saving score...";

        pauseButton.setVisible(false);
        retryButton.setVisible(true);
        homeButton.setVisible(true);
        retryButton.setEnabled(false);
        homeButton.setEnabled(false);

        final int playerId = selectedPlayer.getId();
        final int finalScore = session.getScore();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground()
                    throws SQLException {

                int characterId = findCharacterId(
                    selectedCharacter.name()
                );

                scoreService.saveScore(
                    playerId,
                    characterId,
                    finalScore
                );

                return null;
            }

            @Override
            protected void done() {

                try {
                    get();
                    scoreSaved = true;
                    saveStatus = "Score saved successfully!";
                    retryButton.setEnabled(true);
                    homeButton.setEnabled(true);

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    saveStatus = "Save interrupted.";

                } catch (ExecutionException ex) {
                    saveStatus = "Score save failed!";
                    System.err.println(
                        "Score save error: " + ex.getCause()
                    );
                }

                repaint();
            }
        };

        worker.execute();
    }

    // Main game loop
    private void updateGame() {

        long now = System.nanoTime();
        long elapsedMillis =
                Math.max(0, (now - lastUpdateNanos) / 1_000_000L);

        lastUpdateNanos = now;

        if (session.isGameOver()) {
            gameTimer.stop();
            saveFinalScore();
            return;
        }

        // Freeze everything while paused
        if (paused) {
            repaint();
            return;
        }

        // Advance only active gameplay time
        session.updateTime(elapsedMillis);

        moveButt();

        spawnCounter++;

        if (spawnCounter >= SPAWN_INTERVAL) {
            spawnObject();
            spawnCounter = 0;
        }

        Rectangle playerBounds = new Rectangle(
            playerX + 10,
            playerY + 10,
            PLAYER_WIDTH - 20,
            PLAYER_HEIGHT - 20
        );

        Iterator<FallingObject> iterator =
                fallingObjects.iterator();

        while (iterator.hasNext()) {

            FallingObject object = iterator.next();
            object.update();

            if (object.getBounds().intersects(playerBounds)) {

                switch (object.getType()) {

                    case BROWN:
                        session.addScore(1);
                        break;

                    case GOLD:
                        session.addScore(5);
                        break;

                    case RAINBOW:
                        session.catchRainbow();
                        break;

                    case BOMB:
                        session.catchBomb();
                        break;

                    case TISSUE:
                        session.catchTissue();
                        break;
                }

                iterator.remove();

                if (session.isGameOver()) {
                    gameTimer.stop();
                    saveFinalScore();
                    break;
                }

            } else if (object.getY() > GAME_HEIGHT) {
                iterator.remove();
            }
        }

        if (!session.isGameOver()) {
            while (session.consumeBombRush()) {
                spawnBombRush();
            }
        }

        repaint();
    }

    // Draw game
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (background != null) {
            g.drawImage(
                background, 0, 0,
                GAME_WIDTH, GAME_HEIGHT, this
            );
        }

        if (buttImage != null) {
            g.drawImage(
                buttImage,
                buttX, BUTT_Y,
                BUTT_WIDTH, BUTT_HEIGHT, this
            );
        }

        for (FallingObject object : fallingObjects) {

            Image image = null;

            switch (object.getType()) {

                case BROWN:
                    image = brownPoop;
                    break;

                case GOLD:
                    image = goldPoop;
                    break;

                case RAINBOW:
                    image = rainbowPoop;
                    break;

                case BOMB:
                    image = bombImage;
                    break;

                case TISSUE:
                    image = tissueImage;
                    break;
            }

            if (image != null) {
                g.drawImage(
                    image,
                    object.getX(),
                    object.getY(),
                    object.getWidth(),
                    object.getHeight(),
                    this
                );
            }
        }

        if (character != null) {
            g.drawImage(
                character,
                playerX, playerY,
                PLAYER_WIDTH, PLAYER_HEIGHT, this
            );
        }

        g.setFont(new Font("Arial", Font.BOLD, 22));

        g.setColor(Color.WHITE);
        g.drawString(
            "Score: " + session.getScore(), 20, 35
        );

        g.setColor(Color.RED);
        g.drawString(
            "Hearts: " + session.getHearts(), 20, 65
        );

        g.setColor(Color.MAGENTA);
        g.drawString(
            "Rainbow: " + session.getRainbowCount() + "/5",
            20, 95
        );

        if (session.isBoostActive()) {
            g.setColor(Color.YELLOW);
            g.drawString(
                "10X BOOST: "
                + session.getBoostSecondsRemaining() + "s",
                20, 125
            );
        }

        if (paused && !session.isGameOver()) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 42));
            g.drawString("PAUSED", 210, 220);
        }

        if (session.isGameOver()) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 42));
            g.drawString("GAME OVER", 160, 210);

            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString(
                "Final Score: " + session.getScore(),
                190, 255
            );

            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.setColor(Color.YELLOW);
            g.drawString(saveStatus, 160, 290);
        }
    }
}
