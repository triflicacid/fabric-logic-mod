package net.triflicacid.logicmod.block.custom.wire;

import net.triflicacid.logicmod.util.WireColor;

public class YellowWireBlock extends WireBlock {
    public static final WireColor COLOR = WireColor.YELLOW;
    public static final String BLOCK_NAME = getBlockName(COLOR);
    public static final String ITEM_NAME = getItemName(COLOR);

    public YellowWireBlock() {
        super(COLOR);
    }
}
