package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.custom.wire.AbstractWireBlock;
import net.triflicacid.logicmod.block.custom.wire.WireAdapterBlock;

public abstract class SignalRecieverBlock extends SignalEmitterBlock {
    public SignalRecieverBlock(int initialPower) {
        super(initialPower);
    }

    /** Get power being received in a given direction */
    protected int getPower(World world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos dstPos = pos.offset(direction);
        BlockState dstState = world.getBlockState(dstPos);
        return dstState.getStrongRedstonePower(world, dstPos, direction);
    }
}
