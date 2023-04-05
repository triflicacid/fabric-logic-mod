package net.triflicacid.logicmod.block.custom.logicgate;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.triflicacid.logicmod.Util;
import net.triflicacid.logicmod.block.custom.SignalIOBlock;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;

import java.util.function.Function;

public abstract class LogicGateBlock extends SignalIOBlock implements AdvancedWrenchable {
    public static final int TICK_DELAY = 2;
    private final int numberOfInputs;


    public LogicGateBlock(int numberOfInputs, boolean initiallyActive) {
        super(initiallyActive ? 15 : 0);
        if (numberOfInputs < 1 || numberOfInputs > 3)
            throw new IllegalStateException("Invalid number of inputs to logic gate: " + numberOfInputs);
        this.numberOfInputs = numberOfInputs;
    }

    public abstract boolean logicalFunction(boolean[] inputs);

    @Override
    public final int getSignalStrength(BlockState state, World world, BlockPos pos) {
        return logicalFunction(this.areInputsRecievingPower(world, pos, state)) ? 15 : 0;
    }

    protected Direction[] getInputDirections(Direction facing) {
        switch (numberOfInputs) {
            case 1:
                return new Direction[] { facing };
            case 2:
                return new Direction[] { facing.rotateYClockwise(), facing.rotateYCounterclockwise() };
            case 3:
                return new Direction[] { facing, facing.rotateYClockwise(), facing.rotateYCounterclockwise() };
        }
        return new Direction[0];
    }

    /** Return array indicating if each direction returned by getInputDirections is recieving power */
    public boolean[] areInputsRecievingPower(World world, BlockPos pos, BlockState state) {
        Direction[] directions = getInputDirections(state.get(FACING));
        boolean[] recieving = new boolean[directions.length];

        for (int i = 0; i < directions.length; i++) {
            recieving[i] = this.getPower(world, pos, state, directions[i]) > 0;
        }

        return recieving;
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return TICK_DELAY;
    }

    public abstract boolean isNotVariant();

    public abstract LogicGateBlock getInverse();

    @Override
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        LogicGateBlock inverse = this.getInverse();
        return inverse == null ? null : inverse.getDefaultState().with(FACING, state.get(FACING));
    }
}
