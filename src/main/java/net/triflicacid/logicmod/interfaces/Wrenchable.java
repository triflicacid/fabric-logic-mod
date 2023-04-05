package net.triflicacid.logicmod.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface Wrenchable {
    BlockState applyWrench(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing);
}
