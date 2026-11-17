
package com.catchapoop.model;

public enum CharacterType {

    TOILET("Official Toilet.png"),
    TRASHCAN("Official Trashcan.png"),
    BUCKET("Official Bucket.png"),
    PACMAN("Official Pacman.png");

    private final String imageFile;

    CharacterType(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getImageFile() {
        return imageFile;
    }
}
