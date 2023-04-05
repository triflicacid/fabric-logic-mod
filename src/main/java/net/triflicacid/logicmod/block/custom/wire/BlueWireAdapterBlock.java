package net.triflicacid.logicmod.block.custom.wire;

import net.triflicacid.logicmod.util.WireColor;

public class BlueWireAdapterBlock extends WireAdapterBlock {
    public static final WireColor COLOR = WireColor.BLUE;
    public static final String BLOCK_NAME = getBlockName(COLOR);
    public static final String ITEM_NAME = getItemName(COLOR);

    public BlueWireAdapterBlock() {
        super(COLOR);
    }
}
