package net.triflicacid.logicmod.block.logicgate;

import net.triflicacid.logicmod.util.Util;
import net.triflicacid.logicmod.block.ModBlocks;

/**
 * A logical NAND gate (not variant of AND)
 */
public class NandGateBlock extends LogicGateBlock {
    public static final String NAME = "nand_gate";

    public NandGateBlock() {
        super(2, 3, true);
    }

    @Override
    public boolean logicalFunction(boolean[] inputs) {
        return Util.logicalNand(inputs);
    }

    @Override
    public String getType() {
        return "nand";
    }

    @Override
    public boolean isNotVariant() {
        return true;
    }

    @Override
    public LogicGateBlock getInverse() {
        return ModBlocks.AND_GATE;
    }
}
