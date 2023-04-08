package net.triflicacid.logicmod.block.custom.logicgate;

import net.triflicacid.logicmod.util.Util;
import net.triflicacid.logicmod.block.ModBlocks;

public class BufferGateBlock extends LogicGateBlock {
    public static final String NAME = "buffer_gate";

    public BufferGateBlock() {
        super(1, false);
    }

    @Override
    public boolean logicalFunction(boolean[] inputs) {
        return Util.logicalBuffer(inputs);
    }

    @Override
    public boolean isNotVariant() {
        return false;
    }

    @Override
    public LogicGateBlock getInverse() {
        return ModBlocks.NOT_GATE;
    }
}
