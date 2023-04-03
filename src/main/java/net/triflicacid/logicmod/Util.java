package net.triflicacid.logicmod;

public class Util {
    public static boolean logicalBuffer(boolean[] inputs) {
        if (inputs.length != 1)
            throw new IllegalArgumentException("Expected array of size 1, got " + inputs.length);
        return inputs[0];
    }

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

    public static boolean logicalXor(boolean[] inputs) {
        int truthy = 0;
        for (boolean input : inputs) {
            if (input) {
                if (truthy > 0)
                    return false;

                truthy++;
            }
        }

        return truthy == 1;
    }

    public static boolean logicalXnor(boolean[] inputs) {
        for (int i = 1; i < inputs.length; i++)
            if (inputs[i - 1] != inputs[i])
                return false;

        return true;
    }
}
