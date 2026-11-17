
package com.catchapoop;

import com.catchapoop.database.DatabaseInitializer;
import com.catchapoop.model.Player;
import com.catchapoop.model.CharacterType;
import com.catchapoop.ui.GamePanel;
import com.catchapoop.ui.HomePanel;
import com.catchapoop.ui.PlayerManagementFrame;
import com.catchapoop.ui.LeaderboardFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

public class CatchAPoop {

    public static void main(String[] args) {

        // Initialize SQLite database
        try {
            DatabaseInitializer.initialize();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                null,
                "Database initialization failed: "
                + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // Start Java Swing application
        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Catch a Poop");

            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            HomePanel homePanel = new HomePanel();

            mainPanel.add(homePanel, "HOME");

            // PLAY BUTTON
            homePanel.getPlayButton().addActionListener(e -> {

                Player player = homePanel.getSelectedPlayer();
                CharacterType character =
                        homePanel.getSelectedCharacter();

                if (player == null || character == null) {

                    JOptionPane.showMessageDialog(
                        frame,
                        "Please select a player and character!"
                    );

                    return;
                }

                startGame(
                    frame,
                    mainPanel,
                    cardLayout,
                    homePanel,
                    player,
                    character
                );
            });

            // PLAYER MANAGEMENT BUTTON
            homePanel.getManageButton().addActionListener(e -> {

                PlayerManagementFrame managementFrame =
                        new PlayerManagementFrame();

                managementFrame.addWindowListener(
                    new WindowAdapter() {

                        @Override
                        public void windowClosed(WindowEvent event) {
                            homePanel.loadPlayers();
                        }
                    }
                );

                managementFrame.setVisible(true);
            });

            // LEADERBOARD BUTTON
            homePanel.getLeaderboardButton().addActionListener(e -> {

                LeaderboardFrame leaderboardFrame =
                        new LeaderboardFrame();

                leaderboardFrame.setVisible(true);
            });

            // Main window setup
            frame.setContentPane(mainPanel);

            frame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
            );

            frame.setResizable(false);

            showScreen(
                frame,
                mainPanel,
                cardLayout,
                "HOME",
                600,
                450
            );

            frame.setVisible(true);
        });
    }

    // Start a new gameplay session
    private static void startGame(
            JFrame frame,
            JPanel mainPanel,
            CardLayout cardLayout,
            HomePanel homePanel,
            Player player,
            CharacterType character) {

        GamePanel gamePanel = new GamePanel(
            player,
            character
        );

        mainPanel.add(gamePanel, "GAME");

        // RETRY BUTTON
        gamePanel.setOnRetry(() -> {

            gamePanel.stopGame();
            mainPanel.remove(gamePanel);

            startGame(
                frame,
                mainPanel,
                cardLayout,
                homePanel,
                player,
                character
            );
        });

        // HOME BUTTON
        gamePanel.setOnHome(() -> {

            gamePanel.stopGame();
            mainPanel.remove(gamePanel);

            homePanel.loadPlayers();

            showScreen(
                frame,
                mainPanel,
                cardLayout,
                "HOME",
                600,
                450
            );
        });

        // Resize window to portrait
        showScreen(
            frame,
            mainPanel,
            cardLayout,
            "GAME",
            450,
            700
        );

        gamePanel.requestFocusInWindow();
    }

    // Switch screens and resize the window
    private static void showScreen(
            JFrame frame,
            JPanel mainPanel,
            CardLayout cardLayout,
            String screenName,
            int width,
            int height) {

        cardLayout.show(mainPanel, screenName);

        // Resize the content area, excluding title bar
        mainPanel.setPreferredSize(
            new Dimension(width, height)
        );

        mainPanel.revalidate();

        // Pack adjusts the outer frame for title bar/borders
        frame.pack();

        frame.setLocationRelativeTo(null);

        mainPanel.repaint();
    }
}
