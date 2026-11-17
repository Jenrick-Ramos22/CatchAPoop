package com.catchapoop.ui;

import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads and draws the pixel-art images used in Catch a Poop.
 * This keeps image-related code separate from gameplay logic.
 */
public class GameAssets {

    // Stores loaded images so we do not reload them every frame.
    private final Map<String, Image> images = new HashMap<>();

    // Load all required images when the game starts.
    public void load(String[] filenames) {

        for (String filename : filenames) {

            URL url = getClass().getResource("/images/" + filename);

            if (url != null) {
                images.put(filename, new ImageIcon(url).getImage());
            } else {
                System.err.println("Missing image: " + filename);
            }
        }
    }

    // Retrieve an image by its filename.
    public Image get(String filename) {
        return images.get(filename);
    }

    // Draw an image at the requested position and size.
    public void draw(Graphics g, String filename,
                     int x, int y, int width, int height,
                     java.awt.image.ImageObserver observer) {

        Image image = get(filename);

        if (image != null) {
            g.drawImage(image, x, y, width, height, observer);
        }
    }
}