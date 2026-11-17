
package com.catchapoop.ui;

import com.catchapoop.model.FallingObject;
import com.catchapoop.model.FallingObjectType;
import com.catchapoop.model.GameSession;
import com.catchapoop.model.CharacterType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel {

    // Game screen settings
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

    // Spawn one normal object every 50 timer updates
    private static final int SPAWN_INTERVAL = 50;

    // Number of bombs in each Bomb Rush
    private static final int BOMB_RUSH_COUNT = 6;

    // Game images
    private final Image background;
    private final Image character;
    private final Image brownPoop;
    private final Image goldPoop;
    private final Image rainbowPoop;
    private final Image bombImage;
    private final Image buttImage;

    // Game logic
    private final GameSession session = new GameSession();
    private final Random random = new Random();

    // Multiple falling objects
    private final ArrayList<FallingObject> fallingObjects =
            new ArrayList<>();

    // Player position
    private int playerX = 250;
    private final int playerY = 350;

    // Butt position and movement
    private int buttX = 250;
    private int buttDirection = 1;

    // Spawn counter
    private int spawnCounter = 0;

    // Game timer
    private final Timer gameTimer;

    public GamePanel(CharacterType selectedCharacter) {

        setPreferredSize(
            new Dimension(GAME_WIDTH, GAME_HEIGHT)
        );

        setFocusable(true);

        // Load images
        background = loadImage("Catch a Poop Background.png");
        character = loadImage(selectedCharacter.getImageFile());
        brownPoop = loadImage("Official Normal Poop.gif");
        goldPoop = loadImage("Official Gold Poop.gif");
        rainbowPoop = loadImage("Official Rainbow Poop.gif");
        bombImage = loadImage("Official Bomb.png");
        buttImage = loadImage("Official Butt.png");

        setupControls();

        // Update game approximately 60 times per second
        gameTimer = new Timer(16, e -> updateGame());
        gameTimer.start();
    }

    // Load images from Maven resources
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

                if (session.isGameOver()) {
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

                if (session.isGameOver()) {
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

    // Move the butt from side to side
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

    // Spawn brown, gold, rainbow poop, or bomb
    private void spawnObject() {

        int objectX = buttX
                + (BUTT_WIDTH - POOP_SIZE) / 2;

        int chance = random.nextInt(100);

        FallingObjectType type;

        // 25% bomb, 10% rainbow,
        // 20% gold, 45% brown
        if (chance < 25) {
            type = FallingObjectType.BOMB;

        } else if (chance < 35) {
            type = FallingObjectType.RAINBOW;

        } else if (chance < 55) {
            type = FallingObjectType.GOLD;

        } else {
            type = FallingObjectType.BROWN;
        }

        FallingObject object = new FallingObject(
            objectX,
            BUTT_Y + BUTT_HEIGHT,
            POOP_SIZE,
            POOP_SIZE,
            POOP_SPEED,
            type
        );

        fallingObjects.add(object);
    }

    // Spawn six additional bombs during Bomb Rush
    private void spawnBombRush() {

        for (int i = 0; i < BOMB_RUSH_COUNT; i++) {

            int bombX = random.nextInt(
                GAME_WIDTH - POOP_SIZE + 1
            );

            FallingObject bomb = new FallingObject(
                bombX,
                -POOP_SIZE - (i * 35),
                POOP_SIZE,
                POOP_SIZE,
                POOP_SPEED,
                FallingObjectType.BOMB
            );

            fallingObjects.add(bomb);
        }

        System.out.println("BOMB RUSH ACTIVATED!");
    }

    // Main game update
    private void updateGame() {

        if (session.isGameOver()) {
            gameTimer.stop();
            return;
        }

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
                }

                iterator.remove();

                if (session.isGameOver()) {
                    gameTimer.stop();
                    break;
                }

            } else if (object.getY() > GAME_HEIGHT) {

                iterator.remove();
            }
        }

        // Trigger Bomb Rush for every crossed milestone
        if (!session.isGameOver()) {
            while (session.consumeBombRush()) {
                spawnBombRush();
            }
        }

        repaint();
    }

    // Draw game graphics
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (background != null) {
            g.drawImage(
                background,
                0, 0,
                GAME_WIDTH, GAME_HEIGHT,
                this
            );
        }

        // Draw moving butt
        if (buttImage != null) {
            g.drawImage(
                buttImage,
                buttX, BUTT_Y,
                BUTT_WIDTH, BUTT_HEIGHT,
                this
            );
        }

        // Draw falling objects
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

        // Draw player
        if (character != null) {
            g.drawImage(
                character,
                playerX, playerY,
                PLAYER_WIDTH, PLAYER_HEIGHT,
                this
            );
        }

        // Display game information
        g.setFont(new Font("Arial", Font.BOLD, 22));

        g.setColor(Color.WHITE);
        g.drawString(
            "Score: " + session.getScore(),
            20, 35
        );

        g.setColor(Color.RED);
        g.drawString(
            "Hearts: " + session.getHearts(),
            20, 65
        );

        g.setColor(Color.MAGENTA);
        g.drawString(
            "Rainbow: " + session.getRainbowCount() + "/5",
            20, 95
        );

        // Display rainbow boost timer
        if (session.isBoostActive()) {
            g.setColor(Color.YELLOW);

            g.drawString(
                "10X BOOST: "
                + session.getBoostSecondsRemaining()
                + "s",
                20, 125
            );
        }

        // Display Game Over
        if (session.isGameOver()) {

            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(
                0, 0,
                GAME_WIDTH, GAME_HEIGHT
            );

            g.setColor(Color.WHITE);
            g.setFont(
                new Font("Arial", Font.BOLD, 42)
            );

            g.drawString(
                "GAME OVER",
                160, 210
            );

            g.setFont(
                new Font("Arial", Font.BOLD, 24)
            );

            g.drawString(
                "Final Score: " + session.getScore(),
                190, 255
            );
        }
    }
}
