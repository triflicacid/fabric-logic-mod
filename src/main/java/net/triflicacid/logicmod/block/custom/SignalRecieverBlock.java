package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class SignalRecieverBlock extends SignalEmitterBlock {
    public SignalRecieverBlock(int initialPower) {
        super(initialPower);
    }

    /** Get power being received in a given direction */
    protected int getPower(World world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos dstPos = pos.offset(direction);
        BlockState dstState = world.getBlockState(dstPos);
        int i = dstState.getStrongRedstonePower(world, dstPos, direction);
        return i;
//        int i = world.getEmittedRedstonePower(dstPos, direction);
//        if (i >= 15) {
//            return i;
//        } else {
//            BlockState blockState = world.getBlockState(dstPos);
//            return Math.max(i, blockState.isOf(Blocks.REDSTONE_WIRE) ? blockState.get(RedstoneWireBlock.POWER) : 0);
//        }
    }
}
