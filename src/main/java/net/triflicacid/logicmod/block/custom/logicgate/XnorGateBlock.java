package net.triflicacid.logicmod.block.custom.logicgate;

import net.triflicacid.logicmod.util.Util;
import net.triflicacid.logicmod.block.ModBlocks;

public class XnorGateBlock extends LogicGateBlock {
    public static final String NAME = "xnor_gate";

    public XnorGateBlock() {
        super(2, 3, true);
    }

    @Override
    public boolean logicalFunction(boolean[] inputs) {
        return Util.logicalXnor(inputs);
    }

    @Override
    public boolean isNotVariant() {
        return true;
    }

    @Override
    public LogicGateBlock getInverse() {
        return ModBlocks.XOR_GATE;
    }
}
