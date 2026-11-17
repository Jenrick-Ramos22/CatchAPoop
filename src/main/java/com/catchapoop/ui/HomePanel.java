
package com.catchapoop.ui;

import com.catchapoop.model.CharacterType;
import com.catchapoop.model.Player;
import com.catchapoop.service.PlayerService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HomePanel extends JPanel {

    // Pixel-game colors
    private static final Color SKY =
            new Color(188, 231, 245);

    private static final Color CREAM =
            new Color(255, 248, 232);

    private static final Color DARK_GREEN =
            new Color(54, 91, 66);

    private static final Color SOFT_GREEN =
            new Color(128, 191, 119);

    private static final Color BORDER_GREEN =
            new Color(60, 112, 69);

    private static final Color SAND =
            new Color(233, 214, 168);

    // Player and character selectors
    private final JComboBox<Player> playerBox;
    private final JComboBox<CharacterType> characterBox;

    // Main buttons
    private final JButton playButton;
    private final JButton manageButton;
    private final JButton leaderboardButton;
    private final JButton refreshButton;

    private final JLabel messageLabel;

    private final PlayerService service = new PlayerService();

    public HomePanel() {

        setPreferredSize(new Dimension(600, 450));
        setBackground(SKY);
        setLayout(new BorderLayout(10, 10));

        setBorder(new EmptyBorder(18, 40, 15, 40));

        // Game title
        JLabel title = new JLabel(
            "CATCH A POOP!",
            SwingConstants.CENTER
        );

        title.setFont(
            new Font("Monospaced", Font.BOLD, 34)
        );

        title.setForeground(DARK_GREEN);

        JLabel subtitle = new JLabel(
            "CATCH - SCORE - SURVIVE",
            SwingConstants.CENTER
        );

        subtitle.setFont(
            new Font("Monospaced", Font.BOLD, 12)
        );

        subtitle.setForeground(BORDER_GREEN);

        JPanel titlePanel = new JPanel(
            new GridLayout(2, 1, 0, 2)
        );

        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        add(titlePanel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(
            new GridBagLayout()
        );

        contentPanel.setBackground(CREAM);

        contentPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    BORDER_GREEN, 3
                ),
                new EmptyBorder(15, 20, 15, 20)
            )
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(4, 0, 4, 0);

        // Player label
        JLabel playerLabel = createLabel(
            "SELECT PLAYER"
        );

        gbc.gridy = 0;
        contentPanel.add(playerLabel, gbc);

        // Player dropdown
        playerBox = new JComboBox<>();
        styleComboBox(playerBox);

        gbc.gridy = 1;
        contentPanel.add(playerBox, gbc);

        // Character label
        JLabel characterLabel = createLabel(
            "SELECT CHARACTER"
        );

        gbc.gridy = 2;
        contentPanel.add(characterLabel, gbc);

        // Character dropdown
        characterBox = new JComboBox<>(
            CharacterType.values()
        );

        styleComboBox(characterBox);

        gbc.gridy = 3;
        contentPanel.add(characterBox, gbc);

        // Play button
        playButton = new JButton("PLAY GAME");

        styleButton(
            playButton,
            SOFT_GREEN,
            BORDER_GREEN
        );

        playButton.setFont(
            new Font("Monospaced", Font.BOLD, 18)
        );

        gbc.gridy = 4;
        gbc.insets = new Insets(12, 0, 6, 0);

        contentPanel.add(playButton, gbc);

        // Secondary buttons
        manageButton = new JButton("PLAYERS");
        leaderboardButton = new JButton("LEADERBOARD");

        styleButton(
            manageButton,
            SAND,
            BORDER_GREEN
        );

        styleButton(
            leaderboardButton,
            SAND,
            BORDER_GREEN
        );

        JPanel buttonRow = new JPanel(
            new GridLayout(1, 2, 10, 0)
        );

        buttonRow.setOpaque(false);
        buttonRow.add(manageButton);
        buttonRow.add(leaderboardButton);

        gbc.gridy = 5;
        gbc.insets = new Insets(4, 0, 4, 0);

        contentPanel.add(buttonRow, gbc);

        // Refresh players button
        refreshButton = new JButton("REFRESH PLAYERS");

        styleButton(
            refreshButton,
            CREAM,
            BORDER_GREEN
        );

        gbc.gridy = 6;
        contentPanel.add(refreshButton, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // Bottom status
        messageLabel = new JLabel(
            "Loading players...",
            SwingConstants.CENTER
        );

        messageLabel.setFont(
            new Font("Monospaced", Font.PLAIN, 12)
        );

        messageLabel.setForeground(DARK_GREEN);

        add(messageLabel, BorderLayout.SOUTH);

        // Refresh action
        refreshButton.addActionListener(
            e -> loadPlayers()
        );

        // Load players on startup
        loadPlayers();
    }

    // Create styled label
    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(
            new Font("Monospaced", Font.BOLD, 13)
        );

        label.setForeground(DARK_GREEN);

        return label;
    }

    // Style dropdown
    private void styleComboBox(JComboBox<?> comboBox) {

        comboBox.setFont(
            new Font("Monospaced", Font.PLAIN, 14)
        );

        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(DARK_GREEN);
        comboBox.setFocusable(false);

        comboBox.setPreferredSize(
            new Dimension(350, 34)
        );
    }

    // Style buttons
    private void styleButton(
            JButton button,
            Color background,
            Color border) {

        button.setFont(
            new Font("Monospaced", Font.BOLD, 12)
        );

        button.setBackground(background);
        button.setForeground(DARK_GREEN);

        button.setFocusPainted(false);
        button.setOpaque(true);

        button.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    border, 2
                ),
                new EmptyBorder(7, 10, 7, 10)
            )
        );

        button.setCursor(
            Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR
            )
        );
    }

    // Load saved players from SQLite
    public void loadPlayers() {

        // Remember current selection
        Player currentPlayer =
                (Player) playerBox.getSelectedItem();

        Integer selectedId = currentPlayer == null
                ? null : currentPlayer.getId();

        playButton.setEnabled(false);
        refreshButton.setEnabled(false);

        messageLabel.setText("Loading players...");

        SwingWorker<List<Player>, Void> worker =
                new SwingWorker<>() {

            @Override
            protected List<Player> doInBackground()
                    throws SQLException {

                return service.getAllPlayers();
            }

            @Override
            protected void done() {

                try {
                    List<Player> players = get();

                    playerBox.removeAllItems();

                    Player restoredSelection = null;

                    for (Player player : players) {

                        playerBox.addItem(player);

                        if (selectedId != null
                                && player.getId() == selectedId) {

                            restoredSelection = player;
                        }
                    }

                    if (restoredSelection != null) {
                        playerBox.setSelectedItem(
                            restoredSelection
                        );
                    }

                    messageLabel.setText(
                        "Loaded " + players.size()
                        + " player(s)."
                    );

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    messageLabel.setText(
                        "Loading interrupted."
                    );

                } catch (ExecutionException e) {

                    messageLabel.setText(
                        "Database error: "
                        + e.getCause().getMessage()
                    );

                } finally {

                    refreshButton.setEnabled(true);

                    playButton.setEnabled(
                        playerBox.getItemCount() > 0
                    );
                }
            }
        };

        worker.execute();
    }

    // Get selected player
    public Player getSelectedPlayer() {
        return (Player) playerBox.getSelectedItem();
    }

    // Get selected character
    public CharacterType getSelectedCharacter() {
        return (CharacterType) characterBox.getSelectedItem();
    }

    // Buttons used by CatchAPoop.java
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
