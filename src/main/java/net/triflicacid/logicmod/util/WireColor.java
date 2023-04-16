package net.triflicacid.logicmod.util;

import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

public enum WireColor implements StringIdentifiable {
    BLUE("blue"),
    GREEN("green"),
    ORANGE("orange"),
    PURPLE("purple"),
    RED("red"),
    YELLOW("yellow");

    private final String name;

    WireColor(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public Formatting getFormatting() {
        return switch (this) {
            case BLUE -> Formatting.BLUE;
            case GREEN -> Formatting.GREEN;
            case ORANGE -> Formatting.GOLD;
            case PURPLE -> Formatting.LIGHT_PURPLE;
            case RED -> Formatting.RED;
            case YELLOW -> Formatting.YELLOW;
        };
    }
}