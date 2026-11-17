
package com.catchapoop.ui;

import com.catchapoop.model.CharacterType;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {

    private final JTextField playerNameField;
    private final JComboBox<CharacterType> characterBox;

    private final JButton playButton;
    private final JButton manageButton;
    private final JButton leaderboardButton;

    public HomePanel() {

        setPreferredSize(new Dimension(600, 450));
        setLayout(new BorderLayout(10, 10));
        setBorder(
            BorderFactory.createEmptyBorder(25, 40, 25, 40)
        );

        // Game title
        JLabel title = new JLabel(
            "CATCH A POOP!",
            SwingConstants.CENTER
        );

        title.setFont(
            new Font("Arial", Font.BOLD, 32)
        );

        add(title, BorderLayout.NORTH);

        // Main menu
        JPanel menu = new JPanel(
            new GridLayout(7, 1, 8, 8)
        );

        JLabel nameLabel = new JLabel("Player Name:");

        playerNameField = new JTextField();

        JLabel characterLabel =
                new JLabel("Select Character:");

        characterBox = new JComboBox<>(
            CharacterType.values()
        );

        playButton = new JButton("PLAY");
        manageButton = new JButton("MANAGE PLAYERS");
        leaderboardButton = new JButton("LEADERBOARD");

        menu.add(nameLabel);
        menu.add(playerNameField);
        menu.add(characterLabel);
        menu.add(characterBox);
        menu.add(playButton);
        menu.add(manageButton);
        menu.add(leaderboardButton);

        add(menu, BorderLayout.CENTER);
    }

    public String getPlayerName() {
        return playerNameField.getText().trim();
    }

    public CharacterType getSelectedCharacter() {
        return (CharacterType) characterBox.getSelectedItem();
    }

    public JButton getPlayButton() {
        return playButton;
    }

    public JButton getManageButton() {
        return manageButton;
    }

    public JButton getLeaderboardButton() {
        return leaderboardButton;
    }
}
