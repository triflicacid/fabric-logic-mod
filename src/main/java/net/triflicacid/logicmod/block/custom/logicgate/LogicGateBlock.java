package net.triflicacid.logicmod.block.custom.logicgate;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.custom.SignalIOBlock;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;

import java.util.function.Function;

public abstract class LogicGateBlock extends SignalIOBlock implements AdvancedWrenchable {
    public static final int TICK_DELAY = 2;
    protected Function<Direction, Direction[]> getInputDirections; // Given current direction block is facing in, return array of directions we accept as inputs

    public LogicGateBlock(Function<Direction, Direction[]> getInputDirections, boolean initiallyActive) {
        super(initiallyActive);
        this.getInputDirections = getInputDirections;
    }

    @Override
    public int getSignalStrength() {
        return 15;
    }

    /** Return array indicating if each direction returned by getInputDirections is recieving power */
    public boolean[] areInputsRecievingPower(World world, BlockPos pos, BlockState state) {
        Direction[] directions = this.getInputDirections.apply(state.get(FACING));
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
