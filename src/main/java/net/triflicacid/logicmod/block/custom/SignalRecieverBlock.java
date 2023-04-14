package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class SignalRecieverBlock extends SignalEmitterBlock {
    public SignalRecieverBlock(int initialPower) {
        super(initialPower);
    }

    /** Get power being received in a given direction */
    public static final int getPower(World world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos dstPos = pos.offset(direction);
        BlockState dstState = world.getBlockState(dstPos);
        return dstState.getStrongRedstonePower(world, dstPos, direction);
    }
}
