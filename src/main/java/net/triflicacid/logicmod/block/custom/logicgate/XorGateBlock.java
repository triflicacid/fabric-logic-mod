package net.triflicacid.logicmod.block.custom.logicgate;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.Util;
import net.triflicacid.logicmod.block.ModBlocks;

public class XorGateBlock extends LogicGateBlock {
    public static final String NAME = "xor_gate";

    public XorGateBlock() {
        super(2, 3, false);
    }

    @Override
    public boolean logicalFunction(boolean[] inputs) {
        return Util.logicalXor(inputs);
    }

    @Override
    public boolean isNotVariant() {
        return false;
    }

    @Override
    public LogicGateBlock getInverse() {
        return ModBlocks.XNOR_GATE;
    }
}
