
package com.catchapoop.model;

public class Player {

    private int id;
    private String name;

    // Constructor
    public Player(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Get player ID
    public int getId() {
        return id;
    }

    // Get player name
    public String getName() {
        return name;
    }

    // Update player name
    public void setName(String name) {
        this.name = name;
    }

    // Display player name in dropdown
    @Override
    public String toString() {
        return name;
    }
}
