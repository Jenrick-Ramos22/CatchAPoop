
package com.catchapoop.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImageTestFrame extends JFrame {

    public ImageTestFrame() {

        setTitle("Catch a Poop - Image Test");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        URL imageURL = getClass().getResource(
            "/images/Catch a Poop Background.png"
        );

        if (imageURL == null) {
            JLabel error = new JLabel(
                "Image not found!",
                SwingConstants.CENTER
            );
            add(error);
            return;
        }

        ImageIcon original = new ImageIcon(imageURL);

        Image scaled = original.getImage().getScaledInstance(
            600, 450, Image.SCALE_SMOOTH
        );

        JLabel imageLabel = new JLabel(
            new ImageIcon(scaled)
        );

        add(imageLabel);
    }
}
