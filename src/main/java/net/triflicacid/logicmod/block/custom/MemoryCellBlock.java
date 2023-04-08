package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;

public class MemoryCellBlock extends SignalIOBlock implements AdvancedWrenchable {
    public static final String NAME = "memory_cell";
    public static final IntProperty MEMORY = IntProperty.of("memory", 0, 15);

    public MemoryCellBlock() {
        super(0);
    }

    private int getControlPower(World world, BlockPos pos, BlockState state) {
        return getPower(world, pos, state, state.get(FACING));
    }

    private int getInputPower(World world, BlockPos pos, BlockState state) {
        return getPower(world, pos, state, state.get(FACING).rotateYClockwise());
    }

    @Override
    public int getSignalStrength(BlockState state, World world, BlockPos pos) {
        return getControlPower(world, pos, state) == 0 ? state.get(MEMORY) : getInputPower(world, pos, state);
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 0;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int power = state.get(POWER);
        int expectedPower = getSignalStrength(state, world, pos);

        if (power != expectedPower) {
            state = state.with(POWER, expectedPower).with(ACTIVE, expectedPower > 0);
            if (getControlPower(world, pos, state) > 0) state = state.with(MEMORY, getInputPower(world, pos, state));
            world.setBlockState(pos, state);
        }
    }

    @Override
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        int newPower = state.get(MEMORY) + (Screen.hasShiftDown() ? (-1) : 1);
        if (newPower < 0) newPower = 15;
        else if (newPower > 15) newPower = 0;
        return state.with(MEMORY, newPower);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MEMORY);
        super.appendProperties(builder);
    }
}
