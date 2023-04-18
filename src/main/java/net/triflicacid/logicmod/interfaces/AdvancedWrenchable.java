package net.triflicacid.logicmod.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Describe a block which may have the advanced wrench applied to it.
 */
public interface AdvancedWrenchable {
    @Nullable
    /** Return the new BlockState to replace the given state with. May return null, in which case no update will be issued. */
    BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing);
}
