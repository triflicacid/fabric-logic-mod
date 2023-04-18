package net.triflicacid.logicmod.util;

import net.minecraft.util.math.Direction;

public enum RelativeDirection {
    INFRONT("in-front"),
    LEFT("left"),
    RIGHT("right"),
    BEHIND("behind");

    private String string;
    RelativeDirection(String s) {
        string = s;
    }

    @Override
    public String toString() {
        return string;
    }

    /** Turn in a relative direction from an absolute direction */
    public static Direction turn(Direction abs, RelativeDirection rel) {
        return switch (rel) {
            case INFRONT -> abs;
            case LEFT -> abs.rotateYCounterclockwise();
            case RIGHT -> abs.rotateYClockwise();
            case BEHIND -> abs.getOpposite();
        };
    }
}
