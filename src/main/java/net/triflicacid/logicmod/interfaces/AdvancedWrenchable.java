package net.triflicacid.logicmod.interfaces;

import net.minecraft.block.BlockState;

public interface AdvancedWrenchable {
    BlockState applyAdvancedWrench(BlockState state, boolean holdingShift);
}
