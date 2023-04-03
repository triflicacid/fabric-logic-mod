package net.triflicacid.logicmod.interfaces;

import net.minecraft.block.BlockState;

public interface AdvancedWrenchable {
    public BlockState onWrenchApplied(BlockState state, boolean holdingShift);
}
