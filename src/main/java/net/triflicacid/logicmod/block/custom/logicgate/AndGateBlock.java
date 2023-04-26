package net.triflicacid.logicmod.block.custom.logicgate;

import net.triflicacid.logicmod.util.Util;
import net.triflicacid.logicmod.block.ModBlocks;

/**
 * A logical AND gate
 */
public class AndGateBlock extends LogicGateBlock {
    public static final String NAME = "and_gate";

    public AndGateBlock() {
        super(2, 3, false);
    }

    @Override
    public boolean logicalFunction(boolean[] inputs) {
        return Util.logicalAnd(inputs);
    }

    @Override
    public String getType() { return "and"; }

    @Override
    public boolean isNotVariant() {
        return false;
    }

    @Override
    public LogicGateBlock getInverse() {
        return ModBlocks.NAND_GATE;
    }
}
