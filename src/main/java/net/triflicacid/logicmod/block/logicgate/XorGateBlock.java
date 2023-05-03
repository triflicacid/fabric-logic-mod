package net.triflicacid.logicmod.block.logicgate;

import net.triflicacid.logicmod.util.Util;
import net.triflicacid.logicmod.block.ModBlocks;

/**
 * A logical XOR gate
 */
public class XorGateBlock extends LogicGateBlock {
    public static final String NAME = "xor_gate";

    public XorGateBlock() {
        super(2, 3, false);
    }

    @Override
    public boolean logicalFunction(boolean[] inputs) {
        int truthy = 0;
        for (boolean input : inputs) {
            if (input) {
                if (truthy > 0)
                    return false;

                truthy++;
            }
        }

        return truthy == 1;
    }

    @Override
    public String getType() {
        return "xor";
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
