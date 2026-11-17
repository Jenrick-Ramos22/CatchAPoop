
package com.catchapoop.ui;

import com.catchapoop.model.CharacterType;
import com.catchapoop.model.Player;
import com.catchapoop.service.PlayerService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HomePanel extends JPanel {

    private final JComboBox<Player> playerBox;
    private final JComboBox<CharacterType> characterBox;

    private final JButton playButton;
    private final JButton manageButton;
    private final JButton leaderboardButton;
    private final JButton refreshButton;

    private final JLabel messageLabel;

    private final PlayerService service = new PlayerService();

    public HomePanel() {

        setPreferredSize(new Dimension(600, 450));
        setLayout(new BorderLayout(10, 10));

        setBorder(
            BorderFactory.createEmptyBorder(20, 40, 20, 40)
        );

        JLabel title = new JLabel(
            "CATCH A POOP!",
            SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 30));
        add(title, BorderLayout.NORTH);

        JPanel menu = new JPanel(
            new GridLayout(8, 1, 6, 6)
        );

        playerBox = new JComboBox<>();
        characterBox = new JComboBox<>(
            CharacterType.values()
        );

        playButton = new JButton("PLAY");
        manageButton = new JButton("MANAGE PLAYERS");
        leaderboardButton = new JButton("LEADERBOARD");
        refreshButton = new JButton("REFRESH PLAYERS");

        menu.add(new JLabel("Select Saved Player:"));
        menu.add(playerBox);
        menu.add(new JLabel("Select Character:"));
        menu.add(characterBox);
        menu.add(playButton);
        menu.add(manageButton);
        menu.add(leaderboardButton);
        menu.add(refreshButton);

        add(menu, BorderLayout.CENTER);

        messageLabel = new JLabel(
            "Loading saved players...",
            SwingConstants.CENTER
        );

        add(messageLabel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadPlayers());

        // Load players when homepage opens
        loadPlayers();
    }

    // Retrieve players from SQLite
    public void loadPlayers() {

        playButton.setEnabled(false);
        refreshButton.setEnabled(false);
        messageLabel.setText("Loading players...");

        SwingWorker<List<Player>, Void> worker = new SwingWorker<>() {

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

                    for (Player player : players) {
                        playerBox.addItem(player);
                    }

                    messageLabel.setText(
                        "Loaded " + players.size() + " player(s)."
                    );

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    messageLabel.setText("Loading interrupted.");

                } catch (ExecutionException ex) {
                    messageLabel.setText(
                        "Database error: " + ex.getCause().getMessage()
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

    public Player getSelectedPlayer() {
        return (Player) playerBox.getSelectedItem();
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
