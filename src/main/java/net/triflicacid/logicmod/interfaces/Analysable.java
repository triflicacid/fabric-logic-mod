package net.triflicacid.logicmod.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

/**
 * Describe a block which may have the analyser used on it.
 *
 * If this method is not present, Analyse.analyse is called.
 */
public interface Analysable {
    List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing);
}
