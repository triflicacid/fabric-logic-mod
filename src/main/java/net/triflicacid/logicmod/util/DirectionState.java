package net.triflicacid.logicmod.util;

import net.minecraft.util.StringIdentifiable;

public enum DirectionState implements StringIdentifiable {
    NONE("none"),
    INPUT("input"),
    OUTPUT("output");

    private final String name;

    DirectionState(String name) {
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
