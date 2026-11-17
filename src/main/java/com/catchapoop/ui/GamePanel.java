
package com.catchapoop.ui;

import com.catchapoop.database.DatabaseConnection;
import com.catchapoop.model.*;
import com.catchapoop.service.ScoreService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class GamePanel extends JPanel {

    // Game dimensions
    private static final int W = 450;
    private static final int H = 700;

    // Player and falling objects
    private static final int PLAYER_Y = 555;
    private static final int PLAYER_SIZE = 95;
    private static final int OBJECT_SIZE = 45;
    private static final int FALL_SPEED = 4;

    // Moving butt
    private static final int BUTT_W = 175;
    private static final int BUTT_H = 110;
    private static final int BUTT_Y = 65;

    // Game systems
    private final GameSession session = new GameSession();
    private final ScoreService scores = new ScoreService();
    private final Random random = new Random();

    private final java.util.List<FallingObject> objects =
            new ArrayList<>();

    private final Player player;
    private final CharacterType characterType;

    private final GameAssets assets = new GameAssets();

    // Artwork rendering is handled by a separate helper class.
    private final GameRenderer renderer = new GameRenderer(assets);

    // Positions
    private int playerX = (W - PLAYER_SIZE) / 2;
    private int buttX = (W - BUTT_W) / 2;
    private int buttDirection = 1;
    private int spawnTicks = 0;

    // Game state
    private boolean paused = false;
    // True only while the left mouse button is held on the playfield.
    private boolean mouseHeld = false;
    private long lastUpdate = System.nanoTime();

    // Database
    private int highScore = 0;
    private boolean saveStarted = false;
    private boolean scoreSaved = false;
    private String saveStatus = "";

    // Navigation
    private Runnable onRetry;
    private Runnable onHome;

    // Buttons
    private final JButton pauseButton = new JButton();
    private final JButton retryButton = new JButton("RETRY");
    private final JButton homeButton = new JButton("HOME");

    private final javax.swing.Timer timer;

    public GamePanel(Player player, CharacterType type) {

        if (player == null || type == null) {
            throw new IllegalArgumentException(
                "Player and character are required."
            );
        }

        this.player = player;
        this.characterType = type;

        setPreferredSize(new Dimension(W, H));
        setLayout(null);
        setFocusable(true);

        loadImages();
        setupControls();
        setupMouseControls();
        setupButtons();
        loadHighScore();

        timer = new javax.swing.Timer(16, e -> updateGame());
        timer.start();
    }

    // Load artwork through the GameAssets helper class.
    private void loadImages() {

        String[] files = {
            "Catch a Poop Background.png",
            "Official Butt.png",
            "Official Normal Poop.gif",
            "Official Gold Poop.gif",
            "Official Rainbow Poop.gif",
            "Official Bomb.png",
            "Official Tissue Roll.png",
            "Official Lives.png",
            "Official Pause.png",
            "Paused Panel.png",
            "Game Over Panel.png",
            characterType.getImageFile()
        };

        assets.load(files);
    }

    // Get an image from GameAssets.
    private Image img(String filename) {
        return assets.get(filename);
    }

    // Draw an image using GameAssets.
    private void draw(Graphics g, String file,
                      int x, int y, int width, int height) {

        assets.draw(g, file, x, y, width, height, this);
    }
    // Keyboard movement
    private void setupControls() {

        InputMap input = getInputMap(
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        ActionMap actions = getActionMap();

        input.put(KeyStroke.getKeyStroke("LEFT"), "left");
        input.put(KeyStroke.getKeyStroke("RIGHT"), "right");

        actions.put("left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayer(-15);
            }
        });

        actions.put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayer(15);
            }
        });
    }

    private void movePlayer(int amount) {

        if (paused || session.isGameOver()) {
            return;
        }

        playerX = Math.max(
            0,
            Math.min(W - PLAYER_SIZE, playerX + amount)
        );

        repaint();
    }

    // Hold the left mouse button and drag to move the player horizontally.
    // Listeners belong to the playfield, so clicking Swing buttons does not move the player.
    private void setupMouseControls() {
        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && !paused && !session.isGameOver()) {
                    mouseHeld = true;
                    moveWithMouse(e.getX());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // BUTTON1_DOWN_MASK also prevents movement after release.
                if (mouseHeld && (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
                    moveWithMouse(e.getX());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) mouseHeld = false;
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    // Keep the player inside the 450-pixel-wide playfield.
    private void moveWithMouse(int mouseX) {
        if (paused || session.isGameOver()) return;
        playerX = Math.max(0, Math.min(W - PLAYER_SIZE, mouseX - PLAYER_SIZE / 2));
        repaint();
    }

    // Setup buttons
    private void setupButtons() {

        pauseButton.setBounds(12, 647, 65, 43);

        Image pause = img("Official Pause.png");

        if (pause != null) {

            pauseButton.setIcon(new ImageIcon(
                pause.getScaledInstance(
                    60, 40, Image.SCALE_SMOOTH
                )
            ));

        } else {
            pauseButton.setText("PAUSE");
        }

        pauseButton.setBorderPainted(false);
        pauseButton.setContentAreaFilled(false);
        pauseButton.setFocusPainted(false);

        retryButton.setBounds(65, 425, 145, 43);
        homeButton.setBounds(240, 425, 145, 43);

        for (JButton button :
                new JButton[]{retryButton, homeButton}) {

            button.setFont(
                new Font("Monospaced", Font.BOLD, 16)
            );

            button.setBackground(
                new Color(255, 225, 175)
            );

            button.setForeground(
                new Color(100, 60, 40)
            );

            button.setFocusPainted(false);

            button.setBorder(
                BorderFactory.createLineBorder(
                    new Color(145, 90, 55), 3
                )
            );

            button.setVisible(false);
            button.setEnabled(false);
        }

        pauseButton.addActionListener(e -> togglePause());

        retryButton.addActionListener(e -> {

            if (scoreSaved && onRetry != null) {
                stopGame();
                onRetry.run();
            }
        });

        homeButton.addActionListener(e -> {

            if (scoreSaved && onHome != null) {
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
        mouseHeld = false; // A held click must not carry through Pause/Resume.

        if (paused) {
            session.pauseGame();
        } else {
            session.resumeGame();
        }

        pauseButton.setToolTipText(
            paused ? "Resume game" : "Pause game"
        );

        lastUpdate = System.nanoTime();

        repaint();
    }

    public void setOnRetry(Runnable action) {
        onRetry = action;
    }

    public void setOnHome(Runnable action) {
        onHome = action;
    }

    public void stopGame() {
        mouseHeld = false;
        timer.stop();
    }

    // Move butt
    private void moveButt() {

        buttX += 7 * buttDirection;

        if (buttX <= 0) {
            buttX = 0;
            buttDirection = 1;

        } else if (buttX >= W - BUTT_W) {
            buttX = W - BUTT_W;
            buttDirection = -1;
        }
    }

    // Spawn falling objects
    private void spawnObject() {

        int chance = random.nextInt(100);

        FallingObjectType type =
            chance < 25 ? FallingObjectType.BOMB :
            chance < 35 ? FallingObjectType.RAINBOW :
            chance < 55 ? FallingObjectType.GOLD :
            chance < 70 ? FallingObjectType.TISSUE :
                          FallingObjectType.BROWN;

        objects.add(new FallingObject(
            buttX + (BUTT_W - OBJECT_SIZE) / 2,
            BUTT_Y + BUTT_H,
            OBJECT_SIZE,
            OBJECT_SIZE,
            FALL_SPEED,
            type
        ));
    }

    // Bomb Rush
    private void spawnBombRush() {

        for (int i = 0; i < 6; i++) {

            objects.add(new FallingObject(
                random.nextInt(W - OBJECT_SIZE + 1),
                -OBJECT_SIZE - i * 35,
                OBJECT_SIZE,
                OBJECT_SIZE,
                FALL_SPEED,
                FallingObjectType.BOMB
            ));
        }
    }

    // Handle collisions
    private void catchObject(FallingObject object) {

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
    }

    // Main game loop
    private void updateGame() {

        long now = System.nanoTime();

        long elapsed = Math.max(
            0,
            (now - lastUpdate) / 1_000_000L
        );

        lastUpdate = now;

        if (session.isGameOver()) {
            timer.stop();
            saveFinalScore();
            return;
        }

        if (paused) {
            return;
        }

        session.updateTime(elapsed);

        moveButt();

        if (++spawnTicks >= 50) {
            spawnObject();
            spawnTicks = 0;
        }

        Rectangle playerBounds = new Rectangle(
            playerX + 10,
            PLAYER_Y + 10,
            PLAYER_SIZE - 20,
            PLAYER_SIZE - 20
        );

        Iterator<FallingObject> iterator =
                objects.iterator();

        while (iterator.hasNext()) {

            FallingObject object = iterator.next();
            object.update();

            if (object.getBounds().intersects(playerBounds)) {

                catchObject(object);
                iterator.remove();

                if (session.isGameOver()) {
                    timer.stop();
                    saveFinalScore();
                    break;
                }

            } else if (object.getY() > H) {
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

    // Load high score from SQLite
    private void loadHighScore() {

        new SwingWorker<Integer, Void>() {

            @Override
            protected Integer doInBackground()
                    throws SQLException {

                return scores.getHighestScore(
                    player.getId()
                );
            }

            @Override
            protected void done() {

                try {
                    highScore = get();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                } catch (ExecutionException e) {
                    System.err.println(
                        "High score error: " + e.getCause()
                    );
                }

                repaint();
            }

        }.execute();
    }

    // Save final score once
    private void saveFinalScore() {

        if (saveStarted) {
            return;
        }

        saveStarted = true;
        saveStatus = "Saving score...";

        pauseButton.setVisible(false);

        retryButton.setVisible(true);
        homeButton.setVisible(true);

        int finalScore = session.getScore();

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground()
                    throws SQLException {

                String sql =
                    "SELECT id FROM characters WHERE name = ?";

                int characterId;

                try (Connection conn =
                        DatabaseConnection.getConnection();
                     PreparedStatement ps =
                        conn.prepareStatement(sql)) {

                    ps.setString(
                        1, characterType.name()
                    );

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                "Character not found."
                            );
                        }

                        characterId = rs.getInt("id");
                    }
                }

                scores.saveScore(
                    player.getId(),
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

                    highScore = Math.max(
                        highScore, finalScore
                    );

                    retryButton.setEnabled(true);
                    homeButton.setEnabled(true);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    saveStatus = "Save interrupted.";

                } catch (ExecutionException e) {
                    saveStatus = "Score save failed!";

                    System.err.println(e.getCause());
                }

                repaint();
            }

        }.execute();
    }

    // Select object sprite
    private String objectImage(FallingObjectType type) {

        switch (type) {

            case GOLD:
                return "Official Gold Poop.gif";

            case RAINBOW:
                return "Official Rainbow Poop.gif";

            case BOMB:
                return "Official Bomb.png";

            case TISSUE:
                return "Official Tissue Roll.png";

            default:
                return "Official Normal Poop.gif";
        }
    }

    // Draw game
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        draw(g, "Catch a Poop Background.png",
             0, 0, W, H);

        draw(g, "Official Butt.png",
             buttX, BUTT_Y, BUTT_W, BUTT_H);

        for (FallingObject object : objects) {

            draw(g, objectImage(object.getType()),
                 object.getX(),
                 object.getY(),
                 object.getWidth(),
                 object.getHeight());
        }

        draw(g, characterType.getImageFile(),
             playerX, PLAYER_Y,
             PLAYER_SIZE, PLAYER_SIZE);

        renderer.drawHUD(g, session, highScore, this);

        if (paused && !session.isGameOver()) {
            renderer.drawPauseScreen(g, this);
        }

        if (session.isGameOver()) {
            renderer.drawGameOver(g, session, saveStatus, this);
        }
    }

}
