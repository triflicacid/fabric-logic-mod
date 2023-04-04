package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;

import java.util.function.Function;

public abstract class LogicGateBlock extends SignalRecieverBlock implements AdvancedWrenchable {
    protected Function<Direction, Direction[]> getInputDirections; // Given current direction block is facing in, return array of directions we accept as inputs

    public LogicGateBlock(Function<Direction, Direction[]> getInputDirections, boolean initiallyActive) {
        super(15, initiallyActive);
        this.getInputDirections = getInputDirections;
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
        return 2;
    }

    public abstract boolean isNotVariant();

    public abstract LogicGateBlock getInverse();

    public BlockState applyAdvancedWrench(BlockState state, boolean holdingShift) {
        LogicGateBlock inverse = this.getInverse();
        return inverse == null ? null : inverse.getDefaultState().with(FACING, state.get(FACING));
    }
}
