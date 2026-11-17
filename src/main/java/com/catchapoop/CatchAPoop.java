
package com.catchapoop;

import com.catchapoop.model.CharacterType;
import com.catchapoop.ui.GamePanel;
import com.catchapoop.ui.HomePanel;
import com.catchapoop.ui.PlayerManagementFrame;

import javax.swing.*;
import java.awt.*;

public class CatchAPoop {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Catch a Poop");

            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            HomePanel homePanel = new HomePanel();

            mainPanel.add(homePanel, "HOME");

            // PLAY BUTTON
            homePanel.getPlayButton().addActionListener(e -> {

                String playerName = homePanel.getPlayerName();

                if (playerName.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        frame,
                        "Please enter a player name!"
                    );
                    return;
                }

                CharacterType selectedCharacter =
                        homePanel.getSelectedCharacter();

                GamePanel gamePanel =
                        new GamePanel(selectedCharacter);

                mainPanel.add(gamePanel, "GAME");
                cardLayout.show(mainPanel, "GAME");

                gamePanel.requestFocusInWindow();
            });

            // MANAGE PLAYERS BUTTON
            homePanel.getManageButton().addActionListener(e -> {

                PlayerManagementFrame managementFrame =
                        new PlayerManagementFrame();

                managementFrame.setDefaultCloseOperation(
                    JFrame.DISPOSE_ON_CLOSE
                );

                managementFrame.setVisible(true);
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
}
