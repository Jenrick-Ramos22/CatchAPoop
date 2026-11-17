
package com.catchapoop.ui;

import com.catchapoop.model.Player;
import com.catchapoop.service.PlayerService;
import com.catchapoop.exception.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class PlayerManagementFrame extends JFrame {

    private final JTextField nameField;
    private final JButton addButton;
    private final JLabel messageLabel;

    private final PlayerService service = new PlayerService();

    public PlayerManagementFrame() {

        setTitle("Catch a Poop - Player Management");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JLabel titleLabel = new JLabel(
            "CATCH A POOP - PLAYER MANAGEMENT",
            SwingConstants.CENTER
        );

        nameField = new JTextField();
        addButton = new JButton("Add Player");
        messageLabel = new JLabel(
            "Enter a player name.",
            SwingConstants.CENTER
        );

        panel.add(titleLabel);
        panel.add(nameField);
        panel.add(addButton);
        panel.add(messageLabel);

        add(panel);

        addButton.addActionListener(e -> addPlayer());
    }

    private void addPlayer() {

        String name = nameField.getText();

        addButton.setEnabled(false);
        messageLabel.setText("Saving player...");

        SwingWorker<Player, Void> worker = new SwingWorker<>() {

            @Override
            protected Player doInBackground()
                    throws ValidationException, SQLException {

                return service.createPlayer(name);
            }

            @Override
            protected void done() {

                try {
                    Player player = get();

                    messageLabel.setText(
                        "Player saved! ID: " + player.getId()
                    );

                    nameField.setText("");

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    messageLabel.setText("Operation interrupted.");

                } catch (ExecutionException ex) {
                    Throwable cause = ex.getCause();

                    if (cause instanceof ValidationException) {
                        messageLabel.setText(cause.getMessage());

                    } else if (cause instanceof SQLException) {
                        messageLabel.setText(
                            "Database error: " + cause.getMessage()
                        );

                    } else {
                        messageLabel.setText("Unexpected error.");
                    }

                } finally {
                    addButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }
}
