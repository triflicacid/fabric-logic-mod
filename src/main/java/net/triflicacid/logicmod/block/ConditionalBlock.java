package net.triflicacid.logicmod.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static net.triflicacid.logicmod.util.Util.*;

/**
 * A redstone component which propagates the right or left signal depending on whether we're receiving power, respectively.
 */
public class ConditionalBlock extends AbstractPowerBlock {
    public static final String NAME = "conditional";

    public ConditionalBlock() {
        super(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).breakInstantly(), true);
    }

    /** Get signal from "on" direction */
    protected int getOnPower(World world, BlockState state, BlockPos pos) {
        return getPower(world, pos, state, state.get(FACING).rotateYClockwise());
    }

    /** Get signal from "off" direction */
    protected int getOffPower(World world, BlockState state, BlockPos pos) {
        return getPower(world, pos, state, state.get(FACING).rotateYCounterclockwise());
    }

    /** Get the signal strength that we should propagate, depending on whether we're receiving power */
    public int getSignalStrength(World world, BlockState state, BlockPos pos) {
        return getPower(world, pos, state, state.get(FACING)) > 0
                ? getOnPower(world, state, pos)
                : getOffPower(world, state, pos);
    }

    @Override
    public void update(World world, BlockState state, BlockPos pos, Direction from) {
        if (world.isClient)
            return;

        int power = getSignalStrength(world, state, pos);
        if (power != state.get(POWER)) {
            world.setBlockState(pos, state.with(POWER, power));
        }
    }

    @Override
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        List<Text> messages = new ArrayList<>();

        boolean active = getPower(world, pos, state, state.get(FACING)) > 0;
        messages.add(Text.literal("Power: ").append(numberToText(state.get(POWER))));
        messages.add(Text.literal("State: ").append(booleanToText(active, "on", "off")));
        messages.add(Text.literal("Power if ").append(truthyToText("on")).append(": ").append(numberToText(getOnPower(world, state, pos))));
        messages.add(Text.literal("Power if ").append(falsyToText("off")).append(": ").append(numberToText(getOffPower(world, state, pos))));

        return messages;
    }
}
