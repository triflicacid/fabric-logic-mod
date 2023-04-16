package net.triflicacid.logicmod.util;

import net.minecraft.block.Block;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /** Merge second power map into the second */
    public static void mergePowerMaps(Map<WireColor,Integer> map1, Map<WireColor,Integer> map2) {
        int value;
        for (WireColor color : map2.keySet()) {
            if (map1.containsKey(color)) {
                if (map1.get(color) < (value = map2.get(color))) {
                    map1.put(color, value);
                }
            } else {
                map1.put(color, map2.get(color));
            }
        }
    }

    /** Capitalise a string */
    public static String capitalise(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    /** Get block's name */
    public static String getBlockName(Block block) {
        return Registries.BLOCK.getId(block).toString();
    }

    /** Convert number to text */
    public static MutableText numberToText(int i) {
        return Text.literal(String.valueOf(i)).formatted(Formatting.GOLD);
    }

    public static MutableText numberToText(float f) {
        return Text.literal(String.valueOf(f)).formatted(Formatting.GOLD);
    }

    /** Convert a truthy value to text */
    public static MutableText truthyToText(String s) {
        return Text.literal(s).formatted(Formatting.GREEN);
    }

    /** Convert a falsy value to text */
    public static MutableText falsyToText(String s) {
        return Text.literal(s).formatted(Formatting.RED);
    }

    /** Convert boolean to text */
    public static MutableText booleanToText(boolean b) {
        return b ? truthyToText("true") : falsyToText("false");
    }

    /** Convert boolean to text */
    public static MutableText booleanToText(boolean b, String ifTrue, String ifFalse) {
        return b ? truthyToText(ifTrue) : falsyToText(ifFalse);
    }

    /** Convert special string to text */
    public static MutableText specialToText(String s) {
        return Text.literal(s).formatted(Formatting.AQUA);
    }

    /** Comment to text */
    public static MutableText commentToText(String s) {
        return Text.literal(s).formatted(Formatting.GRAY, Formatting.ITALIC);
    }

    /** Block to text */
    public static MutableText blockToText(Block block) {
        return specialToText(getBlockName(block));
    }

    /** Join a List */
    public static <T> String joinList(List<T> list, String joiner) {
        return list.stream().map(Object::toString).collect(Collectors.joining(joiner));
    }

    /** Hex code to text color: syntax "XXXXXX" */
    public static Style hexToColor(String hexCode) {
        return Style.EMPTY.withColor(Integer.parseInt(hexCode, 16));
    }

    /** NoteBlock note to text */
    public static MutableText noteToText(int note) {
        if (note >= 0 && note < 25) {
            String symbol = noteSymbols[note < 13 ? note : note - 12];
            Style color = hexToColor(noteHexCodes[note]);
            return Text.literal("").append(Text.literal(symbol).fillStyle(color));
        } else {
            return commentToText("unknown");
        }
    }

    /** Get noteblock pitch from note */
    public static float getNotePitch(int note) {
        return (float)Math.pow(2.0, (double)(note - 12) / 12.0);
    }

    private static final String[] noteSymbols = { "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#" };
    private static final String[] noteHexCodes = { "77D700", "95C000", "B2A500", "CC8600", "E26500", "F34100", "FC1E00", "FE000F", "F70033", "E8005A", "CF0083", "AE00A9", "8600CC", "5B00E7", "2D00F9", "020AFE", "0037F6", "0068E0", "009ABC", "00C68D", "00E958", "00FC21", "1FFC00", "59E800", "94C100" };

    /** Wrap a property: if less than min, set max and vica versa */
    public static int wrapInt(int value, int min, int max) {
        if (value < min) return max;
        if (value > max) return min;
        return value;
    }

    /** Spawn redstone particles */
    public static void spawnRedstoneParticles(World world, BlockPos pos) {
        double d = 0.5625;
        Random random = world.random;

        for(Direction direction : Direction.values()) {
            BlockPos blockPos = pos.offset(direction);
            if (!world.getBlockState(blockPos).isOpaqueFullCube(world, blockPos)) {
                Direction.Axis axis = direction.getAxis();
                double dx = axis == Direction.Axis.X ? 0.5 + d * (double)direction.getOffsetX() : (double)random.nextFloat();
                double dy = axis == Direction.Axis.Y ? 0.5 + d * (double)direction.getOffsetY() : (double)random.nextFloat();
                double dz = axis == Direction.Axis.Z ? 0.5 + d * (double)direction.getOffsetZ() : (double)random.nextFloat();
                world.addParticle(DustParticleEffect.DEFAULT, (double)pos.getX() + dx, (double)pos.getY() + dy, (double)pos.getZ() + dz, 0.0, 0.0, 0.0);
            }
        }
    }
}
