
package com.catchapoop.ui;

import com.catchapoop.model.Player;
import com.catchapoop.service.PlayerService;
import com.catchapoop.exception.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PlayerManagementFrame extends JFrame {

    private final JTextField nameField;
    private final JButton addButton;
    private final JButton viewButton;
    private final JButton updateButton;
    private final JButton deleteButton;
    private final JLabel messageLabel;

    private final DefaultTableModel tableModel;
    private final JTable playerTable;

    private final PlayerService service = new PlayerService();

    public PlayerManagementFrame() {

        setTitle("Catch a Poop - Player Management");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel title = new JLabel(
            "CATCH A POOP - PLAYER MANAGEMENT",
            SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        // Player name input
        nameField = new JTextField();

        addButton = new JButton("Add Player");
        viewButton = new JButton("View Players");
        updateButton = new JButton("Update Player");
        deleteButton = new JButton("Delete Player");

        JPanel inputPanel = new JPanel(
            new GridLayout(5, 1, 5, 5)
        );

        inputPanel.add(nameField);
        inputPanel.add(addButton);
        inputPanel.add(viewButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);

        // Player table
        tableModel = new DefaultTableModel(
            new Object[]{"Player ID", "Player Name"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        playerTable = new JTable(tableModel);
        playerTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        // Display selected player's name in text field
        playerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && playerTable.getSelectedRow() >= 0) {

                int row = playerTable.convertRowIndexToModel(
                    playerTable.getSelectedRow()
                );

                nameField.setText(
                    tableModel.getValueAt(row, 1).toString()
                );
            }
        });

        JScrollPane scrollPane = new JScrollPane(playerTable);

        JPanel centerPanel = new JPanel(
            new BorderLayout(10, 10)
        );

        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        messageLabel = new JLabel(
            "Select a player or enter a new name.",
            SwingConstants.CENTER
        );

        panel.add(messageLabel, BorderLayout.SOUTH);
        add(panel);

        // Button actions
        addButton.addActionListener(e -> addPlayer());
        viewButton.addActionListener(e -> loadPlayers());
        updateButton.addActionListener(e -> updatePlayer());
        deleteButton.addActionListener(e -> deletePlayer());

        // Automatically display saved players
        loadPlayers();
    }

    // Disable buttons while database work is running
    private void setButtonsEnabled(boolean enabled) {
        addButton.setEnabled(enabled);
        viewButton.setEnabled(enabled);
        updateButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
    }

    // Get selected player ID
    private Integer getSelectedPlayerId() {

        int selectedRow = playerTable.getSelectedRow();

        if (selectedRow < 0) {
            messageLabel.setText("Please select a player first!");
            return null;
        }

        int modelRow = playerTable.convertRowIndexToModel(
            selectedRow
        );

        return (Integer) tableModel.getValueAt(modelRow, 0);
    }

    // CREATE
    private void addPlayer() {

        String name = nameField.getText();

        setButtonsEnabled(false);
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

                    nameField.setText("");
                    messageLabel.setText(
                        "Player saved! ID: " + player.getId()
                    );

                    loadPlayers();

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    showMessage("Operation interrupted.");

                } catch (ExecutionException ex) {
                    showError(ex.getCause());

                } finally {
                    setButtonsEnabled(true);
                }
            }
        };

        worker.execute();
    }

    // READ
    private void loadPlayers() {

        setButtonsEnabled(false);
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

                    tableModel.setRowCount(0);

                    for (Player player : players) {
                        tableModel.addRow(new Object[]{
                            player.getId(),
                            player.getName()
                        });
                    }

                    messageLabel.setText(
                        "Loaded " + players.size() + " player(s)."
                    );

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    showMessage("Operation interrupted.");

                } catch (ExecutionException ex) {
                    showError(ex.getCause());

                } finally {
                    setButtonsEnabled(true);
                }
            }
        };

        worker.execute();
    }

    // UPDATE
    private void updatePlayer() {

        Integer id = getSelectedPlayerId();
        if (id == null) {
            return;
        }

        String newName = nameField.getText();

        setButtonsEnabled(false);
        messageLabel.setText("Updating player...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {

            @Override
            protected Boolean doInBackground()
                    throws ValidationException, SQLException {
                return service.updatePlayer(id, newName);
            }

            @Override
            protected void done() {
                try {
                    boolean updated = get();

                    if (updated) {
                        loadPlayers();
                    } else {
                        showMessage("Player not found!");
                    }

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    showMessage("Operation interrupted.");

                } catch (ExecutionException ex) {
                    showError(ex.getCause());

                } finally {
                    setButtonsEnabled(true);
                }
            }
        };

        worker.execute();
    }

    // DELETE
    private void deletePlayer() {

        Integer id = getSelectedPlayerId();
        if (id == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete player ID " + id + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        setButtonsEnabled(false);
        messageLabel.setText("Deleting player...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {

            @Override
            protected Boolean doInBackground()
                    throws ValidationException, SQLException {
                return service.deletePlayer(id);
            }

            @Override
            protected void done() {
                try {
                    boolean deleted = get();

                    if (deleted) {
                        nameField.setText("");
                        loadPlayers();
                    } else {
                        showMessage("Player not found!");
                    }

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    showMessage("Operation interrupted.");

                } catch (ExecutionException ex) {
                    showError(ex.getCause());

                } finally {
                    setButtonsEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
    }

    private void showError(Throwable error) {

        if (error instanceof ValidationException) {
            showMessage(error.getMessage());

        } else if (error instanceof SQLException) {
            showMessage("Database error: " + error.getMessage());

        } else {
            showMessage("Unexpected error.");
        }
    }
}
