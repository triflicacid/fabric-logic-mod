package net.triflicacid.logicmod;

public class Util {
    public static boolean logicalNot(boolean[] inputs) {
        if (inputs.length != 1)
            throw new IllegalArgumentException("Expected array of size 1, got " + inputs.length);
        return !inputs[0];
    }

    public static boolean logicalAnd(boolean[] inputs) {
        for (boolean input : inputs)
            if (!input)
                return false;

        return true;
    }

    public static boolean logicalNand(boolean[] inputs) {
        for (boolean input : inputs)
            if (!input)
                return true;

        return false;
    }

    public static boolean logicalOr(boolean[] inputs) {
        for (boolean input : inputs)
            if (input)
                return true;

        return false;
    }

    public static boolean logicalNor(boolean[] inputs) {
        for (boolean input : inputs)
            if (input)
                return false;

        return true;
    }
}
