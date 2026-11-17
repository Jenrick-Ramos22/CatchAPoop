
package com.catchapoop.ui;

import com.catchapoop.service.ScoreService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LeaderboardFrame extends JFrame {

    private final DefaultListModel<String> listModel;
    private final JList<String> leaderboardList;
    private final JButton refreshButton;
    private final JLabel statusLabel;

    private final ScoreService scoreService = new ScoreService();

    public LeaderboardFrame() {

        setTitle("Catch a Poop - Leaderboard");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JLabel title = new JLabel(
            "CATCH A POOP - LEADERBOARD",
            SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 20));

        panel.add(title, BorderLayout.NORTH);

        // Leaderboard list
        listModel = new DefaultListModel<>();

        leaderboardList = new JList<>(listModel);
        leaderboardList.setFont(
            new Font("Arial", Font.PLAIN, 18)
        );

        JScrollPane scrollPane =
                new JScrollPane(leaderboardList);

        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom controls
        JPanel bottomPanel = new JPanel(
            new BorderLayout(5, 5)
        );

        refreshButton = new JButton("Refresh Leaderboard");

        statusLabel = new JLabel(
            "Loading scores...",
            SwingConstants.CENTER
        );

        bottomPanel.add(refreshButton, BorderLayout.NORTH);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);

        refreshButton.addActionListener(e -> loadLeaderboard());

        // Automatically load saved scores
        loadLeaderboard();
    }

    // Retrieve high scores from SQLite
    private void loadLeaderboard() {

        refreshButton.setEnabled(false);
        statusLabel.setText("Loading leaderboard...");

        SwingWorker<List<String>, Void> worker =
                new SwingWorker<>() {

            @Override
            protected List<String> doInBackground()
                    throws SQLException {

                return scoreService.getLeaderboard();
            }

            @Override
            protected void done() {

                try {
                    List<String> scores = get();

                    listModel.clear();

                    if (scores.isEmpty()) {

                        listModel.addElement(
                            "No scores recorded yet."
                        );

                    } else {

                        for (String entry : scores) {
                            listModel.addElement(entry);
                        }
                    }

                    statusLabel.setText(
                        "Loaded " + scores.size()
                        + " leaderboard entries."
                    );

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    statusLabel.setText(
                        "Loading interrupted."
                    );

                } catch (ExecutionException e) {

                    Throwable cause = e.getCause();

                    statusLabel.setText(
                        "Failed to load leaderboard."
                    );

                    System.err.println(
                        "Leaderboard error: " + cause
                    );

                } finally {

                    refreshButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }
}
