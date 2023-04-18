package net.triflicacid.logicmod.util;

import net.minecraft.util.StringIdentifiable;

/**
 * used by adapters, contains the possible states of each face
 */
public enum DirectionState implements StringIdentifiable {
    /** No special behaviour */
    NONE("none"),
    /** Face is acting as a redstone input - write signal to wire */
    INPUT("input"),
    /** face is acting as a redstone output - output internal signal as a redstone signal */
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
