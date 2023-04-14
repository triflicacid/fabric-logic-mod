package net.triflicacid.logicmod.block.custom.adapter;

import net.triflicacid.logicmod.block.custom.adapter.WireAdapterBlock;
import net.triflicacid.logicmod.util.WireColor;

public class RedWireAdapterBlock extends WireAdapterBlock {
    public static final WireColor COLOR = WireColor.RED;
    public static final String NAME = getName(COLOR);

    public RedWireAdapterBlock() {
        super(COLOR);
    }
}
