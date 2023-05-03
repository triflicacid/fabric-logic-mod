package net.triflicacid.logicmod.block.logicgate;

import net.triflicacid.logicmod.util.Util;
import net.triflicacid.logicmod.block.ModBlocks;

/**
 * A logical buffer gate -- propagate the input
 */
public class BufferGateBlock extends LogicGateBlock {
    public static final String NAME = "buffer_gate";

    public BufferGateBlock() {
        super(1, false);
    }

    @Override
    public boolean logicalFunction(boolean[] inputs) {
        if (inputs.length != 1)
            throw new IllegalArgumentException("Expected array of size 1, got " + inputs.length);
        return inputs[0];
    }

    @Override
    public String getType() {
        return "buffer";
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
