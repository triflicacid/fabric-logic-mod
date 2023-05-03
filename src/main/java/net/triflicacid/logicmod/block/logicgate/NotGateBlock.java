package net.triflicacid.logicmod.block.logicgate;

import net.triflicacid.logicmod.util.Util;
import net.triflicacid.logicmod.block.ModBlocks;

/**
 * A logical NOT gate -- inverse the input (not variant of BUFFER)
 */
public class NotGateBlock extends LogicGateBlock {
    public static final String NAME = "not_gate";

    public NotGateBlock() {
        super(1, true);
    }

    @Override
    public boolean logicalFunction(boolean[] inputs) {
        if (inputs.length != 1)
            throw new IllegalArgumentException("Expected array of size 1, got " + inputs.length);
        return !inputs[0];
    }

    @Override
    public String getType() {
        return "not";
    }

    @Override
    public boolean isNotVariant() {
        return true;
    }

    @Override
    public LogicGateBlock getInverse() {
        return ModBlocks.BUFFER_GATE;
    }
}
