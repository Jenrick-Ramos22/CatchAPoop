
package com.catchapoop.model;

import java.awt.Rectangle;

/**
 * Defines the basic actions of a falling game object.
 * Classes implementing this interface must provide
 * movement and collision detection methods.
 */
public interface GameObject {

    // Updates the object's position.
    void update();

    // Returns the object's collision area.
    Rectangle getBounds();
}
