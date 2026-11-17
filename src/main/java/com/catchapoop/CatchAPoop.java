
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

        try {
            DatabaseInitializer.initialize();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                null,
                "Database initialization failed: "
                + e.getMessage()
            );

            return;
        }

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Catch a Poop");

            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            HomePanel homePanel = new HomePanel();

            mainPanel.add(homePanel, "HOME");

            // Starts a new game with selected player
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
                    mainPanel,
                    cardLayout,
                    homePanel,
                    player,
                    character
                );
            });

            // Open player management
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

            // Open leaderboard
            homePanel.getLeaderboardButton().addActionListener(e -> {

                LeaderboardFrame leaderboardFrame =
                        new LeaderboardFrame();

                leaderboardFrame.setVisible(true);
            });

            frame.add(mainPanel);

            frame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
            );

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }

    // Create gameplay and connect navigation
    private static void startGame(
            JPanel mainPanel,
            CardLayout cardLayout,
            HomePanel homePanel,
            Player player,
            CharacterType character) {

        GamePanel gamePanel = new GamePanel(
            player, character
        );

        mainPanel.add(gamePanel, "GAME");

        // RETRY - Start a fresh game
        gamePanel.setOnRetry(() -> {

            gamePanel.stopGame();
            mainPanel.remove(gamePanel);

            startGame(
                mainPanel,
                cardLayout,
                homePanel,
                player,
                character
            );
        });

        // HOME - Return to homepage
        gamePanel.setOnHome(() -> {

            gamePanel.stopGame();
            mainPanel.remove(gamePanel);

            homePanel.loadPlayers();

            cardLayout.show(mainPanel, "HOME");
            mainPanel.revalidate();
            mainPanel.repaint();
        });

        cardLayout.show(mainPanel, "GAME");

        mainPanel.revalidate();
        mainPanel.repaint();

        gamePanel.requestFocusInWindow();
    }
}
