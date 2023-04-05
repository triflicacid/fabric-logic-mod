package net.triflicacid.logicmod.block.custom.logicgate;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.Util;
import net.triflicacid.logicmod.block.ModBlocks;

public class NorGateBlock extends LogicGateBlock {
    public static final String NAME = "nor_gate";

    public NorGateBlock() {
        super(2, true);
    }

    @Override
    public boolean logicalFunction(boolean[] inputs) {
        return Util.logicalNor(inputs);
    }

    @Override
    public boolean isNotVariant() {
        return true;
    }

    @Override
    public LogicGateBlock getInverse() {
        return ModBlocks.OR_GATE;
    }
}
