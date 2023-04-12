package net.triflicacid.logicmod.util;

import net.minecraft.block.*;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static net.triflicacid.logicmod.util.Util.*;

public class Analyse {
    @Nullable
    public static final Text[] analyseBlock(World world, BlockPos pos, BlockState state, Block block) {
        if (block instanceof ButtonBlock buttonBlock) {
            int duration = -1;
            try {
                Field field = ButtonBlock.class.getDeclaredField("pressTicks");
                field.setAccessible(true);
                duration = (Integer) field.get(buttonBlock);
            } catch (NoSuchFieldException e) {}
            catch (IllegalAccessException e) {}

            return new Text[] { Text.literal("State: ").append(booleanToText(state.get(ButtonBlock.POWERED), "on", "off")),
                    Text.literal("Pulse Duration: ").append(duration == -1 ? commentToText("unknown") : numberToText(duration)) };
        }

        if (block instanceof CauldronBlock) {
            return new Text[] { Text.literal("Contents: ").append(commentToText("none")),
                    Text.literal("Level: ").append(numberToText(0)) };
        }

        if (block instanceof LeveledCauldronBlock) {
            String fluid = null;
            if (state.isOf(Blocks.WATER_CAULDRON)) {
                fluid = "water";
            } else if (state.isOf(Blocks.POWDER_SNOW_CAULDRON)) {
                fluid = "powdered snow";
            }

            return new Text[] { Text.literal("Contents: ").append(fluid == null ? commentToText("unknown") : specialToText(fluid)),
                    Text.literal("Level: ").append(numberToText(state.get(LeveledCauldronBlock.LEVEL))) };
        }

        if (block instanceof LavaCauldronBlock) {
            return new Text[] { Text.literal("Contents: ").append(specialToText("lava")),
                    Text.literal("Level: ").append(numberToText(LeveledCauldronBlock.MAX_LEVEL)) };
        }

        if (block instanceof ComparatorBlock) {
            return new Text[] { Text.literal("State: ").append(booleanToText(state.get(ComparatorBlock.POWERED), "on", "off")),
                    Text.literal("Mode: ").append(specialToText(state.get(ComparatorBlock.MODE).toString())) };
        }

        if (block instanceof ComposterBlock) {
            return new Text[] { Text.literal("Level: ").append(numberToText(state.get(ComposterBlock.LEVEL))) };
        }

        if (block instanceof DaylightDetectorBlock) {
            return new Text[] { Text.literal("Power: ").append(numberToText(state.get(DaylightDetectorBlock.POWER))),
                    Text.literal("Inverted: ").append(booleanToText(state.get(DaylightDetectorBlock.INVERTED))) };
        }

        if (block instanceof LecternBlock lecturnBlock) {
            return new Text[] { Text.literal("Power: ").append(numberToText(lecturnBlock.getComparatorOutput(state, world, pos))) };
        }

        if (block instanceof LeverBlock) {
            return new Text[] { Text.literal("State: ").append(booleanToText(state.get(LeverBlock.POWERED), "on", "off")) };
        }

        if (block instanceof NoteBlock) {
            int note = state.get(NoteBlock.NOTE);
            return new Text[] { Text.literal("Instrument: ").append(specialToText(state.get(NoteBlock.INSTRUMENT).toString().toLowerCase())),
                    Text.literal("Note: ").append(noteToText(note)).append(" (").append(numberToText(note)).append(")"),
                    Text.literal("Pitch: ").append(numberToText(getNotePitch(note))),
                    Text.literal("Powered: ").append(booleanToText(state.get(NoteBlock.POWERED))) };
        }

        if (block instanceof PressurePlateBlock plateBlock) {
            int duration = -1;
            try {
                Method method = AbstractPressurePlateBlock.class.getDeclaredMethod("getTickRate");
                method.setAccessible(true);
                duration = (Integer) method.invoke(plateBlock);
            } catch (NoSuchMethodException e) {}
            catch (IllegalAccessException e) {}
            catch (InvocationTargetException e) {}

            return new Text[] { Text.literal("State: ").append(booleanToText(state.get(ButtonBlock.POWERED), "on", "off")),
                    Text.literal("Pulse Duration: ").append(duration == -1 ? commentToText("unknown") : numberToText(duration)) };
        }

        if (block instanceof RedstoneLampBlock) {
            return new Text[] { Text.literal("State: ").append(booleanToText(state.get(RedstoneLampBlock.LIT), "on", "off")) };
        }

        if (block instanceof RedstoneTorchBlock) {
            return new Text[] { Text.literal("State: ").append(booleanToText(state.get(RedstoneTorchBlock.LIT), "on", "off")) };
        }

        if (block instanceof RedstoneWireBlock) {
            return new Text[] { Text.literal("Power: ").append(numberToText(state.get(RedstoneWireBlock.POWER))) };
        }

        if (block instanceof RepeaterBlock) {
            return new Text[] { Text.literal("State: ").append(booleanToText(state.get(RepeaterBlock.POWERED), "on", "off")),
                    Text.literal("Delay: ").append(numberToText(state.get(RepeaterBlock.DELAY))),
                    Text.literal("Locked: ").append(booleanToText(state.get(RepeaterBlock.LOCKED))) };
        }

        if (block instanceof TargetBlock) {
            return new Text[] { Text.literal("Power: ").append(numberToText(state.get(Properties.POWER))) };
        }

        return null;
    }
}
