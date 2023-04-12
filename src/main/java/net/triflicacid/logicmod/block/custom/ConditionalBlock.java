package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.ModBlocks;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;

import static net.triflicacid.logicmod.util.Util.booleanToText;
import static net.triflicacid.logicmod.util.Util.numberToText;

public class ConditionalBlock extends SignalIOBlock {
    public static final String NAME = "conditional";

    public ConditionalBlock() {
        super(0);
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 2;
    }

    @Override
    public int getSignalStrength(BlockState state, World world, BlockPos pos) {
        int input = getPower(world, pos, state, state.get(FACING));
        Direction direction = input > 0 ? state.get(FACING).rotateYClockwise() : state.get(FACING).rotateYCounterclockwise();
        return getPower(world, pos, state, direction);
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (world.isClient)
            return;

        boolean isOn = getPower(world, pos, state, state.get(FACING)) > 0;
        player.sendMessage(Text.literal("State: ").append(booleanToText(isOn, "on", "off")));
        player.sendMessage(Text.literal("Power: ").append(numberToText(state.get(POWER))));
    }
}
