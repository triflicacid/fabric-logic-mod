package net.triflicacid.logicmod.util;

import net.minecraft.state.property.IntProperty;

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
        int truthy = 0;
        for (boolean input : inputs) {
            if (input) {
                truthy++;
            }
        }

        return truthy != 1;
    }

    /** Return if every int value in an array is equal */
    public static boolean allEqual(int[] values) {
        for (int i = 1; i < values.length; i++)
            if (values[i - 1] != values[i])
                return false;
        return true;
    }

    /** Mutate array1 to contain so that array1[i] = max(array1[i], array2[i]) */
    public static void arrayMaxValues(int[] array1, int[] array2) {
        int lim = Math.min(array1.length, array2.length);
        for (int i = 0; i < lim; i++)
            array1[i] = Math.max(array1[i], array2[i]);
    }

    /** Create power IntProperty */
    public static IntProperty powerProperty(String name) {
        return IntProperty.of(name, 0, 15);
    }
}
