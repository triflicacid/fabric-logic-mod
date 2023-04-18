package net.triflicacid.logicmod.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * By default, the wrench alters the HorizontalDirectionFacing property, if present.
 * This method may be used to override this default behaviour.
 */
public interface Wrenchable {
    @Nullable
    /** Return the new BlockState to replace the given state with. May return null, in which case no update will be issued. */
    BlockState applyWrench(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing);
}
