
package com.catchapoop.service;

import com.catchapoop.dao.PlayerDAO;
import com.catchapoop.model.Player;
import com.catchapoop.exception.ValidationException;

import java.sql.SQLException;
import java.util.List;

public class PlayerService {

    private final PlayerDAO playerDAO = new PlayerDAO();

    // CREATE - Add a player
    public Player createPlayer(String name)
            throws ValidationException, SQLException {

        String validName = validateName(name);

        return playerDAO.addPlayer(validName);
    }

    // READ - Find one player
    public Player getPlayer(int id)
            throws ValidationException, SQLException {

        validateId(id);

        return playerDAO.getPlayerById(id);
    }

    // READ - Get all players
    public List<Player> getAllPlayers()
            throws SQLException {

        return playerDAO.getAllPlayers();
    }

    // UPDATE - Change a player's name
    public boolean updatePlayer(int id, String newName)
            throws ValidationException, SQLException {

        validateId(id);

        String validName = validateName(newName);

        return playerDAO.updatePlayer(id, validName);
    }

    // DELETE - Remove a player
    public boolean deletePlayer(int id)
            throws ValidationException, SQLException {

        validateId(id);

        return playerDAO.deletePlayer(id);
    }

    // Validate player names
    private String validateName(String name)
            throws ValidationException {

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException(
                "Player name cannot be empty!"
            );
        }

        String trimmedName = name.trim();

        if (trimmedName.length() > 20) {
            throw new ValidationException(
                "Player name must not exceed 20 characters!"
            );
        }

        return trimmedName;
    }

    // Validate player IDs
    private void validateId(int id)
            throws ValidationException {

        if (id <= 0) {
            throw new ValidationException(
                "Player ID must be greater than zero!"
            );
        }
    }
}
