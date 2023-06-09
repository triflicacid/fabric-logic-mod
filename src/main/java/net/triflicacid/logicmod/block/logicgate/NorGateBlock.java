package net.triflicacid.logicmod.block.logicgate;

import net.triflicacid.logicmod.util.Util;
import net.triflicacid.logicmod.block.ModBlocks;

/**
 * A logical NOR gate (not variant of OR)
 */
public class NorGateBlock extends LogicGateBlock {
    public static final String NAME = "nor_gate";

    public NorGateBlock() {
        super(2, 3, true);
    }

    @Override
    public boolean logicalFunction(boolean[] inputs) {
        for (boolean input : inputs)
            if (input)
                return false;

        return true;
    }

    @Override
    public String getType() {
        return "nor";
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
