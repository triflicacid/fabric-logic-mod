package net.triflicacid.logicmod.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface Analysable {
    void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing);
}
