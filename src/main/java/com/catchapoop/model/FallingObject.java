
package com.catchapoop.model;

import java.awt.Rectangle;

public class FallingObject {

    private int x;
    private int y;

    private final int width;
    private final int height;
    private final int speed;

    private final FallingObjectType type;

    // Existing constructor - keeps old GamePanel working
    public FallingObject(int x, int y, int width,
                         int height, int speed) {

        this(x, y, width, height, speed,
             FallingObjectType.BROWN);
    }

    // New constructor - supports different object types
    public FallingObject(int x, int y, int width,
                         int height, int speed,
                         FallingObjectType type) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.type = type;
    }

    public void update() {
        y += speed;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public FallingObjectType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
