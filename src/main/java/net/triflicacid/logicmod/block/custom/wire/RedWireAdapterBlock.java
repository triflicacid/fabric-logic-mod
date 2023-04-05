package net.triflicacid.logicmod.block.custom.wire;

import net.triflicacid.logicmod.util.WireColor;

public class RedWireAdapterBlock extends WireAdapterBlock {
    public static final WireColor COLOR = WireColor.RED;
    public static final String BLOCK_NAME = getBlockName(COLOR);
    public static final String ITEM_NAME = getItemName(COLOR);

    public RedWireAdapterBlock() {
        super(COLOR);
    }
}
