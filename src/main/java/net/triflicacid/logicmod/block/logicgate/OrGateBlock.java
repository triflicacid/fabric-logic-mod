package net.triflicacid.logicmod.block.logicgate;

import net.triflicacid.logicmod.util.Util;
import net.triflicacid.logicmod.block.ModBlocks;

/**
 * A logical OR gate
 */
public class OrGateBlock extends LogicGateBlock {
    public static final String NAME = "or_gate";

    public OrGateBlock() {
        super(2, 3, false);
    }

    @Override
    public boolean logicalFunction(boolean[] inputs) {
        return Util.logicalOr(inputs);
    }

    @Override
    public String getType() {
        return "or";
    }

    @Override
    public boolean isNotVariant() {
        return false;
    }

    @Override
    public LogicGateBlock getInverse() {
        return ModBlocks.NOR_GATE;
    }
}
