package net.triflicacid.logicmod.util;

import net.minecraft.text.Text;
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
        switch (this) {
            case BLUE:
                return Formatting.BLUE;
            case GREEN:
                return Formatting.GREEN;
            case ORANGE:
                return Formatting.GOLD;
            case PURPLE:
                return Formatting.LIGHT_PURPLE;
            case RED:
                return Formatting.RED;
            case YELLOW:
                return Formatting.YELLOW;
        }
        return Formatting.WHITE;
    }
}