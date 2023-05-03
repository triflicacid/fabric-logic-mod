package net.triflicacid.logicmod.block.logicgate;

import net.triflicacid.logicmod.util.Util;
import net.triflicacid.logicmod.block.ModBlocks;

/**
 * A logical XNOR gate (not variant of XOR)
 */
public class XnorGateBlock extends LogicGateBlock {
    public static final String NAME = "xnor_gate";

    public XnorGateBlock() {
        super(2, 3, true);
    }

    @Override
    public boolean logicalFunction(boolean[] inputs) {
        int truthy = 0;
        for (boolean input : inputs) {
            if (input) {
                truthy++;
            }
        }

        return truthy != 1;
    }

    @Override
    public String getType() {
        return "xnor";
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
