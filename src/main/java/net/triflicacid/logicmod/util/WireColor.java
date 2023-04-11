package net.triflicacid.logicmod.util;

import net.minecraft.util.StringIdentifiable;

public enum WireColor implements StringIdentifiable {
    BLUE("blue"),
    GREEN("green"),
    ORANGE("orange"),
    PURPLE("purple"),
    RED("red"),
    WHITE("white"),
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
}