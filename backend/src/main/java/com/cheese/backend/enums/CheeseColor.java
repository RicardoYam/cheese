package com.cheese.backend.enums;

import lombok.Getter;

@Getter
public enum CheeseColor {
    WHITE("white"),
    CREAM("cream"),
    YELLOW("yellow"),
    ORANGE("orange"),
    BROWN("brown");

    private final String color;

    CheeseColor(String color) {
        this.color = color;
    }

    // Valid cheese color
    public static boolean isValidColor(String color) {
        for (CheeseColor c : CheeseColor.values()) {
            if (c.name().equalsIgnoreCase(color)) {
                return true;
            }
        }
        return false;
    }
}